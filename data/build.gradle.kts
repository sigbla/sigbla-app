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
}

val apacheCommonCSVVersion = ext["apacheCommonCSVVersion"]
val klaxonVersion = ext["klaxonVersion"]
val junitVersion = ext["junitVersion"]
val kotlinTestVersion = ext["kotlinTestVersion"]

dependencies {
    implementation(project(":app"))
    implementation("org.apache.commons:commons-csv:$apacheCommonCSVVersion")
    implementation("com.beust:klaxon:$klaxonVersion")

    testImplementation("junit:junit:$junitVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinTestVersion")
}

tasks.jar {
    manifest {
        archiveFileName.set("sigbla-app-data-${project.version}.jar")
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
            artifactId = "sigbla-app-data"

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
}