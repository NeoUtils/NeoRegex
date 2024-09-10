plugins {
    id("com.neo.regex.android-library")
    id("com.neo.regex.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.designSystem)
        }
    }
}
