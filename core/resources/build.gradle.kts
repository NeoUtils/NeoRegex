import extension.config

plugins {
    id("com.neo.regex.android-library")
    id("com.neo.regex.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.components.resources)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = config.basePackage + ".resources"
    generateResClass = always
}