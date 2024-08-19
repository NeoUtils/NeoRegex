@file:Suppress("UnstableApiUsage")

rootProject.name = "NeoRegex"

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
