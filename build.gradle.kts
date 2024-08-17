plugins {
    // This prevents plugins from being loaded multiple times in each subproject's classloader.
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kmp.compose.compiler) apply false
    alias(libs.plugins.kmp.compose) apply false
}