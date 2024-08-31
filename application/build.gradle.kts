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
            implementation(catalog.androidx.multplatform.lifecycle.viewmodel.compose)
            implementation(catalog.androidx.multplatform.lifecycle.runtime.compose)
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
