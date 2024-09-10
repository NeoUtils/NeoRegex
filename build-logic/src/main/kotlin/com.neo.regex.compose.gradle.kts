import extension.catalog

plugins {
    id("com.neo.regex.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

kotlin {

    sourceSets {

        commonMain.dependencies {

            // compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
        }

        val desktopMain by getting {
            dependencies {
                // compose
                implementation(compose.desktop.currentOs)

                // coroutines
                implementation(catalog.kotlinx.coroutines.swing)
            }
        }
    }
}