plugins {
    id("java-library")
    id("maven-publish")
    id("jacoco")
    kotlin("jvm")
    signing
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://mvn.sigbla.app/repository") }
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
}

val klaxonVersion = ext["klaxonVersion"]
val pdsVersion = ext["pdsVersion"]
val ktorVersion = ext["ktorVersion"]
val kotlinxVersion = ext["kotlinxVersion"]
val junitVersion = ext["junitVersion"]
val kotlinTestVersion = ext["kotlinTestVersion"]
val slf4jVersion = ext["slf4jVersion"]

dependencies {
    implementation("sigbla.app:sigbla-pds:$pdsVersion")
    implementation("com.beust:klaxon:$klaxonVersion")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")

    api("io.ktor:ktor-server-core:$ktorVersion")
    api("io.ktor:ktor-server-netty:$ktorVersion")
    api("io.ktor:ktor-server-websockets:$ktorVersion")

    api("org.jetbrains.kotlinx:kotlinx-html-jvm:$kotlinxVersion")

    testImplementation("junit:junit:$junitVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinTestVersion")
}

tasks.jar {
    manifest {
        archiveFileName.set("sigbla-app-core-${project.version}.jar")
    }
}

java {
    withSourcesJar()
    //withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("sigbla") {
            groupId = "sigbla.app"
            artifactId = "sigbla-app-core"

            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "ProjectRepo"
            url = uri(layout.projectDirectory.dir("../.m2/repository"))
        }
    }
}

signing {
    sign(publishing.publications["sigbla"])
}

tasks.withType<JacocoReport> {
    reports {
        xml.required = true
        csv.required = true
        html.required = true
    }
}

tasks.withType<Test> {
    this.testLogging {
        this.showStandardStreams = true
    }

    maxParallelForks = 2
    systemProperty("sigbla.assert", "true")
}