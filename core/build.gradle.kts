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
}

group = "de.clickism"
version = property("library_version").toString()

repositories {
    mavenCentral()
}

dependencies {
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
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<Jar>("sourcesJar") {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}

tasks.register<Jar>("javadocJar") {
    from(tasks.javadoc)
    archiveClassifier.set("javadoc")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            groupId = group.toString()
            artifactId = "configured-core"
            version = version.toString()
            pom {
                name.set("Configured")
                description.set("Format-independent Java library for generating versioned, documented configuration files from code.")
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