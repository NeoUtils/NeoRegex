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

@file:OptIn(ExperimentalKotlinGradlePluginApi::class)
@file:Suppress("UnstableApiUsage")

import extension.catalog
import extension.config
import extension.module
import extension.name
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.neoutils.neoregex.android-library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

group = config.module(name = "core")
version = config.version.name()

kotlin {

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.JETBRAINS)
    }

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm("desktop")

    js("web", IR) {
        browser()
        binaries.library()
    }

    sourceSets {
        commonMain.dependencies {

            // lifecycle
            implementation(catalog.androidx.multplatform.lifecycle.runtime.compose)

            // compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)

            // koin
            implementation(catalog.koin.core)
            implementation(catalog.koin.compose)
        }

        val desktopMain by getting {
            dependencies {
                // compose
                implementation(compose.desktop.currentOs)
            }
        }
    }
}
