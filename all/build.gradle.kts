plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow")
    kotlin("jvm")
    signing
}

group = "sigbla.all"

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

tasks.shadowJar {
    manifest {
        archiveFileName.set("sigbla-app-all-${project.version}.jar")
    }
    archiveClassifier.set("")
}

java {
    //withSourcesJar()
    //withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("sigbla") {
            groupId = "sigbla.app"
            artifactId = "sigbla-app-all"

            //from(components["java"])
            artifact(tasks["shadowJar"])
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
