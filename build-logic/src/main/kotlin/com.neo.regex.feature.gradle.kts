import extension.catalog

plugins {
    id("com.neo.regex.android-library")
    id("com.neo.regex.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {

            // lifecycle
            implementation(catalog.androidx.multplatform.lifecycle.runtime.compose)

            // voyager
            implementation(catalog.voyager.navigator)
            implementation(catalog.voyager.screenModel)
            implementation(catalog.voyager.transitions)
        }
    }
}
