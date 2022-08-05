plugins {
    id("me.him188.maven-central-publish") version "1.0.0-dev-3" apply false
    kotlin("multiplatform") version "1.7.10" apply false
    kotlin("plugin.serialization") version "1.7.10" apply false
}

val kotestVersion: String by project

group = "net.ormr.semver4k"
description = "A Kotlin multiplatform implementation of the semantic versioning 2.0 specification."
version = "0.1.0"

repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}

allprojects {
    group = "net.ormr.semver4k"
    description = "A Kotlin multiplatform implementation of the semantic versioning 2.0 specification."

    repositories {
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    }
}