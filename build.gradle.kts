import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.2.20"
    `java-gradle-plugin`
    `maven-publish`
    signing
    id("com.gradleup.shadow") version "9.2.2"
    id("org.jmailen.kotlinter") version "5.2.0"
    id("com.github.ben-manes.versions") version "0.53.0"
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

group = "org.cs124"
version = "2025.10.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.20.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.20.0")
}
gradlePlugin {
    plugins {
        create("gradleoverlay") {
            id = "org.cs124.gradleoverlay"
            implementationClass = "edu.illinois.cs.cs125.gradleoverlay.Plugin"
        }
    }
}
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withJavadocJar()
    withSourcesJar()
}
tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
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
publishing {
    publications {
        afterEvaluate {
            withType<MavenPublication> {
                pom {
                    name = "gradleoverlay"
                    description = "Gradle overlay plugin for CS 124."
                    url = "https://cs124.org"
                    licenses {
                        license {
                            name = "MIT License"
                            url = "https://opensource.org/license/mit/"
                        }
                    }
                    developers {
                        developer {
                            id = "gchallen"
                            name = "Geoffrey Challen"
                            email = "challen@illinois.edu"
                        }
                    }
                    scm {
                        connection = "scm:git:https://github.com/cs124-illinois/gradleoverlay.git"
                        developerConnection = "scm:git:https://github.com/cs124-illinois/gradleoverlay.git"
                        url = "https://github.com/cs124-illinois/gradleoverlay"
                    }
                }
            }
        }
    }
}
signing {
    setRequired {
        gradle.taskGraph.allTasks.any { it.name.contains("ToSonatype") }
    }
    sign(publishing.publications)
}
tasks.withType<Sign>().configureEach {
    onlyIf {
        gradle.taskGraph.allTasks.any { it.name.contains("ToSonatype") }
    }
}
nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
        }
    }
}
tasks.withType<AbstractPublishToMaven>().configureEach {
    val signingTasks = tasks.withType<Sign>()
    mustRunAfter(signingTasks)
}
