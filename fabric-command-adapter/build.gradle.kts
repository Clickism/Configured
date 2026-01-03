/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

plugins {
    id("java")
    id("maven-publish")
    id("signing")
    id("com.gradleup.nmcp").version("0.1.4")
    id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT"
}

group = "de.clickism"
version = property("library_version").toString()

val minecraftVersion = "1.20.1"

repositories {
    mavenCentral()
    maven("https://maven.nucleoid.xyz")
}

dependencies {
    // Core
    implementation(project(":core"))
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    mappings("net.fabricmc:yarn:${property("deps.yarn_mappings")}:v2")
    // Annotations
    compileOnly("org.jetbrains:annotations:24.0.0")
    // Testing
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(project(":yaml"))
    testImplementation(project(":json"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    withSourcesJar()
    withJavadocJar()
}

tasks.processResources {
    val properties = mapOf(
        "version" to version,
        "targetVersion" to ">=$minecraftVersion",
        "minecraftVersion" to minecraftVersion,
        "fabricVersion" to project.property("deps.fabric_loader"),
    )

    filesMatching("fabric.mod.json") {
        expand(properties)
    }
    inputs.properties(properties)
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = group.toString()
            artifactId = "configured-fabric-command-adapter"
            version = version.toString()
            pom {
                name.set("Configured - Fabric Command Adapter")
                description.set("FabricMC command adapter for the Configured configuration library.")
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

nmcp {
    centralPortal {
        username = findProperty("ossrhUsername") as String?
        password = findProperty("ossrhPassword") as String?
        publishingType = "USER_MANAGED"
    }
}

tasks.named("publish") {
    dependsOn(tasks["sourcesJar"], tasks["javadocJar"])
}