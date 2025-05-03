plugins {
    id("java")
}

group = "me.clickism"
version = "1.0-SNAPSHOT"

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