import extension.catalog

plugins {
    id("com.neo.regex.android-library")
    id("com.neo.regex.multiplatform")
}

kotlin {
    sourceSets {

        desktopMain.dependencies {

            // jewel
            api(catalog.jewel)
            api(catalog.jewel.decorated)
        }
    }
}

