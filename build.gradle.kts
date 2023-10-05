plugins {
    kotlin("jvm") version "1.9.10"
    `java-gradle-plugin`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.jmailen.kotlinter") version "3.16.0"
    id("com.github.ben-manes.versions") version "0.48.0"
}

group = "com.github.cs124-illinois"
version = "2023.10.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.2")
}
gradlePlugin {
    plugins {
        create("plugin") {
            id = "com.github.cs124-illinois.gradleoverlay"
            implementationClass = "edu.illinois.cs.cs125.gradleoverlay.Plugin"
        }
    }
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}
tasks.dependencyUpdates {
    rejectVersionIf {
        listOf("alpha", "beta", "rc", "cr", "m", "preview", "b", "ea", "eap", "pr").any { qualifier ->
            candidate.version.matches(Regex("(?i).*[.-]$qualifier[.\\d-+]*"))
        }
    }
    gradleReleaseChannel = "current"
}

