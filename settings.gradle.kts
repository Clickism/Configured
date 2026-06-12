pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("com.gradleup.nmcp.settings").version("1.4.3")
}

nmcpSettings {
    centralPortal {
        username = providers.gradleProperty("ossrhUsername").orNull
        password = providers.gradleProperty("ossrhPassword").orNull
        publishingType = "USER_MANAGED"
    }
}

rootProject.name = "Configured"

include(
    ":core",
    ":localization",
    ":yaml",
    ":json",
    ":paper-command-adapter",
    ":fabric-command-adapter",
    ":fabric+noremap-command-adapter",
    ":neoforge-command-adapter"
)

