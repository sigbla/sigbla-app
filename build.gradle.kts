plugins {
    kotlin("jvm") version "1.9.20" apply false
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    signing
}

allprojects {
    group = "sigbla.app"
    version = "1.24.0"

    ext {
        set("klaxonVersion", "5.6")
        set("pdsVersion", "1.0")
        set("ktorVersion", "2.3.6")
        set("kotlinxVersion", "0.9.1")
        set("junitVersion", "4.13.2")
        set("kotlinTestVersion", "1.9.20")
        set("slf4jVersion", "2.0.7")
    }
}
