plugins {
    `kotlin-dsl`
}

repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
}

dependencies {

    implementation(libs.plugin.android.gradle)
    implementation(libs.plugin.kotlin.gradle)
    implementation(libs.plugin.compose.compiler)
    implementation(libs.plugin.compose)

    // TODO: remove when safe-typed version catalog for plugin script is available
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
