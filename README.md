This is a Kotlin Multiplatform project targeting Android, iOS, Web, Desktop.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
    - `commonMain` is for code that’s common for all targets.
    - Other folders are for Kotlin code that will be compiled for only the platform indicated in the
      folder name.
      For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
      `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for
  your project.

Learn more
about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack
channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them
on [GitHub](https://github.com/JetBrains/compose-multiplatform/issues).

You can open the web application by running the `:composeApp:wasmJsBrowserDevelopmentRun` Gradle
task.

### Adding desktop run configuration

1. Add new Gradle configuration
2. Enter name: `desktopApp`
3. Enter run: `composeApp:run`
4. Apply

### Compiling
1. Install sdkman https://sdkman.io/install/
2. Install compatible gradle. For now, recommended version is 8.10
```bash
sdk install gradle 8.10
```
3. Install JDK, recommended version: 17.0.14-zulu
```bash
sdk install java 17.0.14-zulu
```
4. Setting up android studio to use gradle and `JAVA_HOME`
Update the gradle to use local installed version of gradle: /home/<user>/.sdkman/candidates/gradle/current
Config to use JAVA_HOME version
5. If running in Android studio giving error, try to run
```bash
gradle composeApp:run
```