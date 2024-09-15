import extension.catalog
import extension.config
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

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

compose.desktop {
    application {
        mainClass = config.basePackage + ".MainKt"

        buildTypes.release {
            proguard {
                isEnabled.set(false)
            }
        }

        nativeDistributions {

            targetFormats(TargetFormat.Exe, TargetFormat.Rpm)

            packageName = "NeoRegex"
            description = "A simple regex tester"
            packageVersion = config.version.name(withPhase = false)

            linux {
                iconFile.set(file("assets/ic_launcher.png"))
                appCategory = "Utility"
            }

            // TODO: not tested on MacOS
            macOS {
                iconFile.set(file("assets/ic_launcher.icns"))
            }

            windows {
                iconFile.set(file("assets/ic_launcher.ico"))
                menu = true
                perUserInstall = true
            }
        }
    }
}
