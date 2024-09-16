import extension.catalog
import extension.config
import extension.properties
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("com.android.application")
    id("com.neo.regex.compose")
}

kotlin {

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.ORACLE) // Oracle OpenJDK
    }

    sourceSets {
        commonMain.dependencies {

            // modules
            implementation(projects.feature.matcher)
            implementation(projects.core.designSystem)
            implementation(projects.core.resources)
            implementation(projects.core.common)

            // voyager
            implementation(catalog.voyager.navigator)
            implementation(catalog.voyager.transitions)
        }

        androidMain.dependencies {

            // activity
            implementation(catalog.androidx.activity.compose)
        }
    }
}

compose.desktop {
    application {
        mainClass = config.basePackage + ".MainKt"

        buildTypes.release {
            proguard {
                isEnabled.set(false)
            }
        }

        nativeDistributions {

            targetFormats(TargetFormat.Exe, TargetFormat.Rpm)

            packageName = "NeoRegex"
            description = "A simple regex tester"
            packageVersion = config.version.name(withPhase = false)

            linux {
                iconFile.set(file("assets/ic_launcher.png"))
                appCategory = "Utility"
            }

            // TODO: not tested on MacOS
            macOS {
                iconFile.set(file("assets/ic_launcher.icns"))
            }

            windows {
                iconFile.set(file("assets/ic_launcher.ico"))
                menu = true
                perUserInstall = true
            }
        }
    }
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

    signingConfigs {
        create("release") {
            rootDir
                .resolve("keystore.properties")
                .properties()?.let {
                    storeFile = it.storeFile
                    storePassword = it.storePassword
                    keyAlias = it.keyAlias
                    keyPassword = it.keyPassword
                }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

