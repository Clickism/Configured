pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        mavenCentral()
        gradlePluginPortal()
    }
}


rootProject.name = "Configured"

include(":core", ":localization", ":yaml", ":json", ":paper-command-adapter", ":fabric-command-adapter")

