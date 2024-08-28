import extension.catalog

plugins {
    id("com.neo.regex.android-app")
    id("com.neo.regex.multiplatform")
    id("com.neo.regex.desktop-app")
}

dependencies {

    // activity
    androidMainImplementation(catalog.androidx.activity)
    androidMainImplementation(catalog.androidx.activity.compose)

    // lifecycle
    commonMainImplementation(catalog.androidx.multplatform.lifecycle.viewmodel)
    commonMainImplementation(catalog.androidx.multplatform.lifecycle.runtime.compose)

    // modules
    commonMainImplementation(projects.designSystem)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.neo.resources"
    generateResClass = always
}