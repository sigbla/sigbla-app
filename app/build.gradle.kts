import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java-library")
    id("application")
    id("org.jetbrains.kotlin.jvm") version "1.4.10"
}

group = "sigbla.app"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
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

    testImplementation("junit:junit:4.12")
}

val compileKotlin: KotlinCompile by tasks
val compileTestKotlin: KotlinCompile by tasks

compileKotlin.kotlinOptions {
    jvmTarget = "11"
}

compileTestKotlin.kotlinOptions {
    jvmTarget = "11"
}
