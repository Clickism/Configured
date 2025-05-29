plugins {
    id("java")
    id("maven-publish")
}

group = "me.clickism"
version = property("library_version").toString()

repositories {
    mavenCentral()
}

dependencies {
    // Core
    implementation(project(":core"))
    // Json
    implementation("com.google.code.gson:gson:2.13.1")
    // Annotations
    compileOnly("org.jetbrains:annotations:24.0.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
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
            artifactId = "configured-json"
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
        }
    }
    repositories {
        maven {
            name = "mavenLocal"
        }
    }
}

tasks.named("publish") {
    dependsOn(tasks["sourcesJar"], tasks["javadocJar"])
}