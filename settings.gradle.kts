@file:Suppress("UnstableApiUsage")

rootProject.name = "NeoRegex"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {

    includeBuild("build-logic")

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
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
