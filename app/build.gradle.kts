plugins {
    id("java-library")
    id("io.ktor.plugin") version "2.3.0"
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
    jcenter()
}

application {
    mainClass.set("")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.10")
    implementation("com.beust:klaxon:5.2")
    implementation("com.github.andrewoma.dexx:kollection:0.7")
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-websockets")

    api("org.jetbrains.kotlinx:kotlinx-html-jvm:0.6.12")

    testImplementation("junit:junit:4.13.2")
}
