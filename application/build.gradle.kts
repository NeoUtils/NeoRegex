import extension.catalog

plugins {
    id("com.neo.regex.android-app")
    id("com.neo.regex.multiplatform")
    id("com.neo.regex.desktop-app")
}

kotlin {
    sourceSets {
        commonMain.dependencies {

            // modules
            implementation(projects.core.designSystem)
            implementation(projects.core.resources)
            implementation(projects.core.sharedUi)

            // lifecycle
            implementation(catalog.androidx.multplatform.lifecycle.runtime.compose)

            // voyager
            implementation(catalog.voyager.navigator)
            implementation(catalog.voyager.screenModel)
            implementation(catalog.voyager.transitions)
        }

        androidMain.dependencies {

            // activity
            implementation(catalog.androidx.activity)
            implementation(catalog.androidx.activity.compose)

            // lifecycle
            implementation(catalog.androidx.lifecycle.viewmodel.compose)
        }
    }
}
