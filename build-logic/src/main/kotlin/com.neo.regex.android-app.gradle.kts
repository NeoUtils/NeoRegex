import extension.catalog
import extension.config

plugins {
    id("com.android.application")
}

android {
    namespace = config.basePackage
    compileSdk = config.android.compileSdk

    defaultConfig {
        applicationId = config.basePackage

        minSdk = config.android.minSdk
        targetSdk = config.android.targetSdk

        versionCode = config.version.code()
        versionName = config.version.name()
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
