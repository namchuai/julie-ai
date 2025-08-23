package ai.julie.feature.pythonrunner

import ai.julie.feature.pythonrunner.domain.ExecuteCode
import ai.julie.feature.pythonrunner.domain.ExecuteCodeAndGetString
import ai.julie.feature.pythonrunner.domain.GetInstalledPackages
import ai.julie.feature.pythonrunner.domain.InstallPackage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.TimeUnit

actual class PythonExecutor actual constructor() :
    GetInstalledPackages,
    ExecuteCode,
    ExecuteCodeAndGetString,
    InstallPackage {

    private val pythonExecutable: String by lazy {
        val userHome = System.getProperty("user.home")
        val julieHome = "$userHome/.julie"
        val cpythonHome = "$julieHome/cpython"
        val pythonBin = "$cpythonHome/bin/python3"

        // Initialize contained CPython installation
        initializePython(cpythonHome)

        pythonBin
    }

    override suspend fun execute(code: String): Any? {
        val result = executePythonScript(code)
        return result
    }

    override suspend fun executeAndGetString(code: String): String {
        val result = executePythonScript(code)
        return result
    }

    private fun executePythonScript(code: String): String {
        // Ensure Python is initialized (this triggers the lazy property)
        val executable = pythonExecutable

        // Create temporary script file
        val tempScript = File.createTempFile("python_script", ".py")
        tempScript.writeText(code)

        try {
            println("Executing Python script with: $executable")
            val processBuilder = ProcessBuilder(executable, tempScript.absolutePath)
            val process = processBuilder.start()

            // Read output
            val output = process.inputStream.bufferedReader().readText()
            val error = process.errorStream.bufferedReader().readText()

            process.waitFor(30, TimeUnit.SECONDS)

            if (process.exitValue() != 0) {
                throw RuntimeException("Python script failed: $error")
            }

            return output
        } finally {
            tempScript.delete()
        }
    }

    private fun initializePython(pythonHome: String) {
        val pythonBin = File("$pythonHome/bin/python3")

        // Check if Python is already installed
        if (pythonBin.exists() && pythonBin.canExecute()) {
            println("CPython already installed at: $pythonHome")
            return
        }

        println("CPython not found. Starting download...")
        downloadAndInstallPython(pythonHome)
    }

    private fun downloadAndInstallPython(pythonHome: String) {
        try {
            val osName = System.getProperty("os.name").lowercase()
            val osArch = System.getProperty("os.arch").lowercase()

            println("Detected OS: $osName, Architecture: $osArch")

            // Determine the correct Python release URL
            val pythonUrl = when {
                osName.contains("mac") && (osArch.contains("aarch64") || osArch.contains("arm")) ->
                    "https://github.com/indygreg/python-build-standalone/releases/download/20241002/cpython-3.11.10+20241002-aarch64-apple-darwin-install_only.tar.gz"

                osName.contains("mac") ->
                    "https://github.com/indygreg/python-build-standalone/releases/download/20241002/cpython-3.11.10+20241002-x86_64-apple-darwin-install_only.tar.gz"

                osName.contains("linux") && (osArch.contains("aarch64") || osArch.contains("arm")) ->
                    "https://github.com/indygreg/python-build-standalone/releases/download/20241002/cpython-3.11.10+20241002-aarch64-unknown-linux-gnu-install_only.tar.gz"

                osName.contains("linux") ->
                    "https://github.com/indygreg/python-build-standalone/releases/download/20241002/cpython-3.11.10+20241002-x86_64-unknown-linux-gnu-install_only.tar.gz"

                osName.contains("windows") ->
                    "https://github.com/indygreg/python-build-standalone/releases/download/20241002/cpython-3.11.10+20241002-x86_64-pc-windows-msvc-shared-install_only.tar.gz"

                else -> throw RuntimeException("Unsupported operating system: $osName ($osArch)")
            }

            println("Downloading CPython from: $pythonUrl")
            println("This may take a few minutes...")

            val pythonDir = File(pythonHome)
            pythonDir.mkdirs()

            // Test URL accessibility first
            val testConnection = java.net.URL(pythonUrl).openConnection()
            testConnection.connectTimeout = 10000
            testConnection.readTimeout = 30000

            println("Testing URL accessibility...")
            val responseCode = (testConnection as java.net.HttpURLConnection).responseCode
            println("HTTP Response Code: $responseCode")

            if (responseCode != 200) {
                throw RuntimeException("Failed to access download URL. HTTP $responseCode")
            }

            // Download the archive
            val tempFile = File.createTempFile("python", ".tar.gz")
            println("Downloading to temporary file: ${tempFile.absolutePath}")

            val url = java.net.URL(pythonUrl)
            val connection = url.openConnection()
            connection.connectTimeout = 30000
            connection.readTimeout = 60000

            connection.getInputStream().use { input ->
                tempFile.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    var totalBytes = 0L
                    var bytesRead: Int

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalBytes += bytesRead

                        // Show progress every 10MB
                        if (totalBytes % (10 * 1024 * 1024) == 0L) {
                            println("Downloaded: ${totalBytes / (1024 * 1024)}MB")
                        }
                    }
                    println("Download complete. Total size: ${totalBytes / (1024 * 1024)}MB")
                }
            }

            println("Extracting archive...")

            // Extract the archive
            extractTarGz(tempFile, pythonDir)

            // Make python executable
            val pythonBin = File("$pythonHome/bin/python3")
            if (pythonBin.exists()) {
                pythonBin.setExecutable(true)
                println("CPython successfully installed to: $pythonHome")
            } else {
                // List what was actually extracted
                println("Contents of $pythonHome:")
                pythonDir.listFiles()?.forEach { println("  ${it.name}") }
                throw RuntimeException("Python binary not found after extraction")
            }

            tempFile.delete()

        } catch (e: Exception) {
            println("Error details: ${e.javaClass.simpleName}: ${e.message}")
            e.printStackTrace()
            throw RuntimeException("Failed to download and install CPython: ${e.message}", e)
        }
    }

    private fun extractTarGz(tarGzFile: File, targetDir: File) {
        // Simple tar.gz extraction using ProcessBuilder
        val processBuilder = ProcessBuilder(
            "tar",
            "-xzf",
            tarGzFile.absolutePath,
            "-C",
            targetDir.absolutePath,
            "--strip-components=1"
        )
        val process = processBuilder.start()
        process.waitFor()

        if (process.exitValue() != 0) {
            throw RuntimeException("Failed to extract tar.gz file")
        }
    }

    override suspend fun installPackage(packageName: String): String {
        println("Installing package: $packageName")
        val processBuilder = ProcessBuilder(
            pythonExecutable,
            "-m",
            "pip",
            "install",
            packageName,
            "--verbose"
        )
        val process = processBuilder.start()

        val output = process.inputStream.bufferedReader().readText()
        val error = process.errorStream.bufferedReader().readText()

        process.waitFor(120, TimeUnit.SECONDS) // Longer timeout for package installation

        println("Exit code: ${process.exitValue()}")
        println("Output: $output")
        println("Error: $error")

        return if (process.exitValue() != 0) {
            "Package installation failed (exit code ${process.exitValue()}): $error\nOutput: $output"
        } else {
            "Successfully installed $packageName\n$output"
        }
    }

    override suspend fun getInstalledPackages(): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val processBuilder = ProcessBuilder(pythonExecutable, "-m", "pip", "list")
                val process = processBuilder.start()

                val output = process.inputStream.bufferedReader().readText()
                process.waitFor(30, TimeUnit.SECONDS)

                output.lines().filter { it.isNotBlank() }
            } catch (e: Exception) {
                listOf("Error getting installed packages: ${e.message}")
            }
        }
    }
}