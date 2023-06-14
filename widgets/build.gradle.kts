plugins {
    id("java-library")
    kotlin("jvm")
}

group = "sigbla.widgets"
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
