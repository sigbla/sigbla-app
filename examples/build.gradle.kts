plugins {
    id("java")
    kotlin("jvm")
}

group = "sigbla.examples"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":app"))
}
