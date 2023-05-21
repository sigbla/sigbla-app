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
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
}

application {
    mainClass.set("")
}

dependencies {
    implementation("com.beust:klaxon:5.5")
    implementation("com.github.andrewoma.dexx:kollection:0.7")
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-websockets")

    api("org.jetbrains.kotlinx:kotlinx-html-jvm:0.8.1")

    testImplementation("junit:junit:4.13.2")
    testImplementation(kotlin("test"))
}
