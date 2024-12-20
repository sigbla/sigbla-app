plugins {
    id("java-library")
    id("maven-publish")
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
}

val klaxonVersion = ext["klaxonVersion"]

dependencies {
    implementation(project(":app"))
    implementation("com.beust:klaxon:$klaxonVersion")
}

tasks.jar {
    manifest {
        archiveFileName.set("sigbla-app-charts-${project.version}.jar")
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
            artifactId = "sigbla-app-charts"

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

tasks.withType<Test> {
    this.testLogging {
        this.showStandardStreams = true
    }
}