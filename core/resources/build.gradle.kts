plugins {
    id("com.neo.regex.android-library")
    id("com.neo.regex.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

dependencies {
    commonMainImplementation(compose.components.resources)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.neo.resources"
    generateResClass = always
}