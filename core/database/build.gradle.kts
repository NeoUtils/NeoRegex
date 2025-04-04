import extension.config
import extension.module

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

plugins {
    alias(libs.plugins.neoutils.neoregex.core)
    alias(libs.plugins.sqldelight)
}

group = config.module(name = "core")

kotlin {
    sourceSets {
        commonMain.dependencies {

            // modules
            api(projects.core.common)
            api(projects.core.datasource)

            // SQLDelight
            implementation(libs.sqldelight.coroutines)

            // Stately
            implementation(libs.stately.common)
        }

        androidMain.dependencies {
            implementation(libs.sqldelight.driver.android)
        }

        desktopMain.dependencies {
            implementation(libs.sqldelight.driver.sqlite)
        }

        webMain.dependencies {
            implementation(libs.sqldelight.driver.sqljs)
        }
    }
}

sqldelight {
    databases {
        create("PatternDatabase") {
            packageName = "com.neoutils.neoregex.core.database.db"
        }
    }

    linkSqlite = true
}
