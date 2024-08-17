@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kmp.compose.compiler)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kmp.compose)
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
            implementation(libs.androidx.activity)
            implementation(libs.androidx.activity.compose)
        }

        commonMain.dependencies {

            // compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.material)
            implementation(compose.components.resources)

            // lifecycle
            implementation(libs.androidx.multplatform.lifecycle.viewmodel)
            implementation(libs.androidx.multplatform.lifecycle.runtime.compose)
        }

        desktopMain.dependencies {

            // compose
            implementation(compose.desktop.currentOs)

            // coroutines
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

android {
    namespace = "com.neo.regex"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.neo.regex"

        minSdk = 24
        targetSdk = 34

        versionCode = 1
        versionName = "2.0.0-alpha"
    }

    buildFeatures {
        compose = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    dependencies {
        debugImplementation(libs.androidx.compose.ui.tooling)
    }
}

compose.desktop {
    application {
        mainClass = "com.neo.regex.ui.MainKt"

        nativeDistributions {

            targetFormats(TargetFormat.Exe, TargetFormat.Rpm)

            packageName = "com.neo.regex"
            packageVersion = "2.0.0"
        }
    }
}