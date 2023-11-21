plugins {
    kotlin("jvm") version "1.9.20" apply false
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    signing
}

allprojects {
    group = "sigbla.app"
    version = "1.23.0-SNAPSHOT"

    ext {
        set("klaxonVersion", "5.6")
        set("dexxVersion", "0.7")
        set("ktorVersion", "2.3.6")
        set("kotlinxVersion", "0.9.1")
        set("junitVersion", "4.13.2")
        set("kotlinTestVersion", "1.9.20")
        set("slf4jVersion", "2.0.7")
    }
}
