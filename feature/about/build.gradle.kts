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
import extension.module

plugins {
    alias(libs.plugins.neoutils.neoregex.core)
}

group = config.module(name = "feature")

kotlin {
    sourceSets {
        commonMain.dependencies {

            // core
            implementation(projects.core.designSystem)
            implementation(projects.core.resources)
            implementation(projects.core.sharedUi)
            implementation(projects.core.common)

            // voyager
            implementation(catalog.voyager.navigator)
            implementation(catalog.voyager.screenModel)
            implementation(catalog.voyager.transitions)

            // about libraries
            implementation(libs.aboutlibraries.compose)

            // highlight
            implementation(libs.highlight.compose)
        }
    }
}