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
    implementation(project(":widgets"))
    implementation("org.slf4j:slf4j-simple:2.0.7")
}
