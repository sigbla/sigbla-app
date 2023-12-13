plugins {
    id("java")
    id("maven-publish")
    kotlin("jvm")
    signing
}

group = "sigbla.examples"

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://mvn.sigbla.app/repository") }
}

val slf4jVersion = ext["slf4jVersion"]

dependencies {
    implementation(project(":app"))
    implementation(project(":widgets"))
    implementation(project(":charts"))
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
}

tasks.jar {
    manifest {
        archiveFileName.set("sigbla-app-examples-${project.version}.jar")
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
            artifactId = "sigbla-app-examples"

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
