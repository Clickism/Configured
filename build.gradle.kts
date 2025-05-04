plugins {
    id("java")
    id("maven-publish")
}

group = "me.clickism"
version = property("library_version").toString()

repositories {
    mavenCentral()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    // Serialization
    implementation("org.snakeyaml:snakeyaml-engine:2.9")

    // Annotations
    compileOnly("org.jetbrains:annotations:24.0.0")

    // Testing
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
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
            artifactId = "configured"
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