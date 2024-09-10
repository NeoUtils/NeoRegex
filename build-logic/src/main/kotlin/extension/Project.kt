package extension

import model.Config
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

val config = Config(
    version = Config.Version(
        major = 2,
        minor = 0,
        patch = 0,
        phase = Config.Phase.ALPHA
    ),
    android = Config.Android(
        compileSdk = 34,
        minSdk = 24,
        targetSdk = 34
    ),
    basePackage = "com.neo.regex"
)

val Project.catalog
    get() = the<LibrariesForLibs>()
