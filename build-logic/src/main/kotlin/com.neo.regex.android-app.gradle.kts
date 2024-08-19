import org.gradle.api.JavaVersion
import org.gradle.kotlin.dsl.dependencies

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.application")
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
        debugImplementation(catalog.androidx.compose.ui.tooling)
    }
}
