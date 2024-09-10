import extension.config

plugins {
    id("com.neo.regex.android-library")
    id("com.neo.regex.compose")
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