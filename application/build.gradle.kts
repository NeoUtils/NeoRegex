import extension.catalog

plugins {
    id("com.neo.regex.android-app")
    id("com.neo.regex.compose")
    id("com.neo.regex.desktop-app")
}

kotlin {

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.ORACLE) // Oracle OpenJDK
    }

    sourceSets {
        commonMain.dependencies {

            // modules
            implementation(projects.feature.matcher)
            implementation(projects.core.designSystem)
            implementation(projects.core.resources)
            implementation(projects.core.common)

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
