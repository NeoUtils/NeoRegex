plugins {
    id("com.neo.regex.feature")
}

kotlin {
    sourceSets {
        commonMain.dependencies {

            // modules
            implementation(projects.core.designSystem)
            implementation(projects.core.resources)
            implementation(projects.core.sharedUi)
            implementation(projects.core.common)
        }
    }
}