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

import com.android.utils.jvmArchitecture
import com.android.utils.osArchitecture
import extension.catalog
import extension.config
import extension.name
import org.jetbrains.kotlin.konan.util.visibleName

plugins {
    alias(libs.plugins.neoutils.neoregex.android)
    alias(libs.plugins.neoutils.neoregex.desktop)
    alias(libs.plugins.neoutils.neoregex.web)
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
            implementation(projects.core.sharedUi)
            implementation(projects.core.datasource)

            // voyager
            implementation(catalog.voyager.navigator)
            implementation(catalog.voyager.transitions)

            // compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
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
