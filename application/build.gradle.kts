import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.android.application)
    id("com.neo.regex.multiplatform")
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