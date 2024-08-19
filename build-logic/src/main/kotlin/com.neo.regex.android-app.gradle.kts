import extension.catalog
import extension.config
import org.gradle.api.JavaVersion
import org.gradle.kotlin.dsl.dependencies

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.application")
}

android {
    namespace = config.basePackage
    compileSdk = config.android.compileSdk

    defaultConfig {
        applicationId = config.basePackage

        minSdk = config.android.minSdk
        targetSdk = config.android.targetSdk

        versionCode = config.app.code()
        versionName = config.app.name()
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
