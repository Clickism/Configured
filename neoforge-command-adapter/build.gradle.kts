/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

plugins {
    id("java")
    id("maven-publish")
    id("signing")
    id("net.neoforged.moddev") version "2.0.137"
}

group = "de.clickism"
version = property("library_version").toString()

val minecraftVersion = "1.21.1"

repositories {
    mavenCentral()
    maven("https://maven.neoforged.net/releases")
}

neoForge {
    version = property("deps.neoforge").toString()
    runs {
        register("client") {
            client()
            gameDirectory = file("run/")
            ideName = "NeoForge Client"
            programArgument("--username=ClickToPlay")
        }
        register("server") {
            server()
            gameDirectory = file("run/")
            ideName = "NeoForge Server"
        }
    }
    mods {
        register(property("mod.id").toString()) {
            sourceSet(sourceSets["main"])
        }
    }
}

dependencies {
    // Core
    implementation(project(":core"))
    implementation("com.mojang:brigadier:1.0.18")
    // Annotations
    compileOnly("org.jetbrains:annotations:24.0.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withSourcesJar()
    withJavadocJar()
}

tasks.processResources {
    val properties = mapOf(
        "version" to version,
        "targetVersion" to ">=$minecraftVersion",
        "minecraftVersion" to minecraftVersion,
    )

    filesMatching(listOf("META-INF/neoforge.mods.toml", "META-INF/mods.toml")) {
        expand(properties)
    }
    inputs.properties(properties)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = group.toString()
            artifactId = "configured-neoforge-command-adapter"
            version = version.toString()
            pom {
                name.set("Configured - Neoforge Command Adapter")
                description.set("Neoforge command adapter for the Configured configuration library.")
                url.set("https://github.com/Clickism/Configured")
                licenses {
                    license {
                        name.set("GNU General Public License v3.0")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.html")
                    }
                }
                developers {
                    developer {
                        id.set("Clickism")
                        name.set("Clickism")
                        email.set("dev@clickism.de")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Clickism/Configured.git")
                    developerConnection.set("scm:git:ssh://github.com/Clickism/Configured.git")
                    url.set("https://github.com/Clickism/Configured")
                }
            }
        }
    }
    signing {
        sign(publishing.publications["mavenJava"])
    }
}