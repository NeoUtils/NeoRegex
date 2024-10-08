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

import extension.config
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

kotlin {
    jvm("desktop")
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