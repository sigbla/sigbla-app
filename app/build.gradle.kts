plugins {
    id("java-library")
    kotlin("jvm")
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
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
}

val klaxonVersion = "5.6"
val dexxVersion = "0.7"
val ktorVersion = "2.3.6"
val kotlinxVersion = "0.9.1"
val junitVersion = "4.13.2"
val kotlinTestVersion = "1.9.20"

dependencies {
    implementation("com.beust:klaxon:$klaxonVersion")
    implementation("com.github.andrewoma.dexx:kollection:$dexxVersion")

    api("io.ktor:ktor-server-core:$ktorVersion")
    api("io.ktor:ktor-server-netty:$ktorVersion")
    api("io.ktor:ktor-server-websockets:$ktorVersion")

    api("org.jetbrains.kotlinx:kotlinx-html-jvm:$kotlinxVersion")

    testImplementation("junit:junit:$junitVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinTestVersion")
}
