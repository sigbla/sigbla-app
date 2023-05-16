import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java-library")
    id("application")
    kotlin("jvm") version "1.8.21"
}

group = "sigbla.app"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    jcenter()
}

val ktorVersion = "1.3.2"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.10")
    implementation("com.beust:klaxon:5.2")
    implementation("com.github.andrewoma.dexx:kollection:0.7")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")

    api("org.jetbrains.kotlinx:kotlinx-html-jvm:0.6.12")

    testImplementation("junit:junit:4.13.2")
}
