import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("application")
    kotlin("jvm") version "1.8.21"
}

group = "sigbla.examples"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(project(":app"))
    testImplementation("junit:junit:4.13.1")
}

val compileKotlin: KotlinCompile by tasks
val compileTestKotlin: KotlinCompile by tasks

compileKotlin.kotlinOptions {
    jvmTarget = "11"
}

compileTestKotlin.kotlinOptions {
    jvmTarget = "11"
}
