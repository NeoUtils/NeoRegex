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

@file:Suppress("UnstableApiUsage")

import com.android.utils.osArchitecture
import com.codingfeline.buildkonfig.compiler.FieldSpec
import extension.catalog
import extension.config
import extension.environment
import extension.name
import org.jetbrains.kotlin.konan.util.visibleName

plugins {
    alias(libs.plugins.neoutils.neoregex.android)
    alias(libs.plugins.neoutils.neoregex.desktop)
    alias(libs.plugins.neoutils.neoregex.web)
    alias(libs.plugins.aboutlibraries)
    alias(libs.plugins.buildkonfig)
}

kotlin {

    sourceSets {
        commonMain.dependencies {

            // core
            implementation(projects.core.designSystem)
            implementation(projects.core.resources)
            implementation(projects.core.common)
            implementation(projects.core.sharedUi)
            implementation(projects.core.datasource)
            implementation(projects.core.database)
            implementation(projects.core.repository)
            implementation(projects.core.manager)

            // feature
            implementation(projects.feature.matcher)
            implementation(projects.feature.about)
            implementation(projects.feature.validator)
            implementation(projects.feature.saved)

            // voyager
            implementation(catalog.voyager.navigator)
            implementation(catalog.voyager.transitions)
            implementation(catalog.voyager.screenModel)
            implementation(catalog.voyager.koin)

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
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
        }

        androidMain.dependencies {

            // koin
            implementation(libs.koin.android)

            // bugsnag
            implementation("com.bugsnag:bugsnag-android:6.+")
            implementation("com.bugsnag:bugsnag-android-performance:1.+")
        }

        val desktopMain by getting {
            dependencies {

                // bugsnag
                implementation("com.bugsnag:bugsnag:3.7.2")
            }
        }
    }
}

tasks.register<Tar>("packageReleaseTarGz") {

    group = "distribution"
    description = "create a zipped genetic distribution"

    dependsOn("createReleaseDistributable")

    compression = Compression.GZIP

    archiveBaseName.set(config.name)
    archiveExtension.set("tar.gz")
    archiveVersion.set(config.version.name())
    archiveClassifier.set(osArchitecture.visibleName)

    destinationDirectory.set(layout.buildDirectory.dir("distribution"))

    into("NeoRegex") {
        from(layout.projectDirectory.dir("installation"))
        from(layout.buildDirectory.dir("compose/binaries/main-release/app/NeoRegex"))
    }
}

aboutLibraries {
    prettyPrint = true
}


buildkonfig {
    packageName = config.basePackage

    defaultConfigs {}

    targetConfigs {

        val environment = rootDir
            .resolve("environment.properties")
            .environment()

        create("android") {
            buildConfigField(
                type = FieldSpec.Type.STRING,
                name = "BUGSNAG_API_KEY",
                value = environment?.bugsnagAndroidApiKey,
                nullable = true
            )
        }

        create("desktop") {
            buildConfigField(
                type = FieldSpec.Type.STRING,
                name = "BUGSNAG_API_KEY",
                value = environment?.bugsnagDesktopApiKey,
                nullable = true
            )
        }
    }
}