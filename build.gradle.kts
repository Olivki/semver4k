plugins {
    id("me.him188.maven-central-publish") version "1.0.0-dev-3"
    kotlin("multiplatform") version "1.7.10"
}

val kotestVersion: String by project

group = "net.ormr.semver4k"
description = "A Kotlin multiplatform implementation of the semantic versioning 2.0 specification."
version = "0.1.0"

repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}

mavenCentralPublish {
    useCentralS01()
    singleDevGithubProject("Olivki", "semver4k")
    licenseApacheV2()
}

kotlin {
    explicitApi()

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js(IR) {
        binaries.library()
        nodejs()
        browser()
    }

    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("io.kotest:kotest-assertions-core:$kotestVersion")
                implementation("io.kotest:kotest-property:$kotestVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
            }
        }
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit5"))
                implementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
            }
        }
        val jsMain by getting
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val nativeMain by getting
        val nativeTest by getting

        all {
            languageSettings {
                optIn("kotlin.Experimental")
            }
        }
    }
}