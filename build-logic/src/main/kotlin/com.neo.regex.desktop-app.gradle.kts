import extension.config
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

compose.desktop {
    application {
        mainClass = "${config.basePackage}.ui.MainKt"

        nativeDistributions {

            targetFormats(TargetFormat.Exe, TargetFormat.Rpm)

            packageName = config.basePackage
            packageVersion = config.app.name(withPhase = false)
        }
    }
}