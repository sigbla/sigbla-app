plugins {
    id("java-library")
    kotlin("jvm")
}

group = "sigbla.charts"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

val klaxonVersion = "5.5"

dependencies {
    implementation(project(":app"))
    implementation("com.beust:klaxon:$klaxonVersion")
}
