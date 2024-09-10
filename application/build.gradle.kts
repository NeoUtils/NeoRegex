import extension.catalog

plugins {
    id("com.neo.regex.android-app")
    id("com.neo.regex.compose")
    id("com.neo.regex.desktop-app")
}

kotlin {
    sourceSets {
        commonMain.dependencies {

            // modules
            implementation(projects.feature.matcher)
            implementation(projects.core.designSystem)
            implementation(projects.core.resources)

            // voyager
            implementation(catalog.voyager.navigator)
            implementation(catalog.voyager.transitions)
        }

        androidMain.dependencies {

            // activity
            implementation(catalog.androidx.activity.compose)
        }
    }
}
