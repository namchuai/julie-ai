package ai.julie.storage

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import ui.julie.storage.AppDatabase
import java.io.File

// TODO: NamH need to rethink about creating the database file
class DesktopDatabaseDriverFactory : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        // Define a stable location in the user's home directory
        val dbDir = File(System.getProperty("user.home"), ".julieApp/db")
        if (!dbDir.exists()) {
            dbDir.mkdirs() // Create the directory if it doesn't exist
        }
        val dbPath = File(dbDir, "launch.db").absolutePath
        println("Using database path: $dbPath")

        // Create the driver using the absolute path
        val driver = JdbcSqliteDriver("jdbc:sqlite:$dbPath")

        // Create the schema using SQLDelight if it doesn't exist
        // Replace 'Database.Schema' if your generated Schema object has a different name
        try {
            AppDatabase.Schema.create(driver)
            println("Database schema verified/created.")
        } catch (e: Exception) {
            // This might happen if the schema partially exists or other DB issues
            println("Warning: Error during schema creation/verification: ${e.message}")
            // Depending on the error, you might want to handle it differently
        }

        return driver
    }
}
