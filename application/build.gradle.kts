/*
 * NeoRegex.
 *
 * Copyright (C) 2024 Irineu A. Silva.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import extension.catalog
import extension.config
import extension.properties
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("com.android.application")
    id("com.neo.regex.compose")
}

group = config.basePackage
version = config.version.name()

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
        mainClass = config.basePackage + ".Main_desktopKt"

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

tasks.register<Tar>("createTarGz") {

    group = "distribution"
    description = "create a zipped genetic distribution"

    dependsOn("createReleaseDistributable")

    compression = Compression.GZIP
    archiveExtension.set("tar.gz")
    archiveFileName.set(config.distName() + ".tar.gz")
    destinationDirectory.set(layout.buildDirectory.dir("distribution"))

    into("NeoRegex") {
        from(layout.projectDirectory.dir("installation"))
        from(layout.buildDirectory.dir("compose/binaries/main-release/app/NeoRegex"))
    }
}
