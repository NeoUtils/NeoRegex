@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

kotlin {

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm("desktop")

    sourceSets {

        val desktopMain by getting

        androidMain.dependencies {

            // activity
            implementation(catalog.androidx.activity)
            implementation(catalog.androidx.activity.compose)
        }

        commonMain.dependencies {

            // compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.material)
            implementation(compose.components.resources)

            // lifecycle
            implementation(catalog.androidx.multplatform.lifecycle.viewmodel)
            implementation(catalog.androidx.multplatform.lifecycle.runtime.compose)
        }

        desktopMain.dependencies {

            // compose
            implementation(compose.desktop.currentOs)

            // coroutines
            implementation(catalog.kotlinx.coroutines.swing)
        }
    }
}