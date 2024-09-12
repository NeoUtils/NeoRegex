@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {

    includeBuild("build-logic")

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

include(":application")
include(":core:design-system")
include(":core:resources")
include(":core:shared-ui")
include(":core:common")
include(":feature:matcher")

rootProject.name = "NeoRegex"
