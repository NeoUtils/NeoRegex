package model

data class Config(
    val version: Version,
    val android: Android,
    val basePackage: String
) {
    data class Version(
        val major: Int,
        val minor: Int,
        val patch: Int,
        val phase: Phase
    ) {

        fun name(
            withPhase: Boolean = true
        ): String {

            if (withPhase && phase != Phase.RELEASE) {
                return "$major.$minor.$patch-${phase.value}"
            }

            return "$major.$minor.$patch"
        }

        fun code(): Int {

            require(patch in 0..9)
            require(minor in 0..9)

            return major * 100 + minor * 10 + patch
        }
    }

    data class Android(
        val compileSdk: Int,
        val minSdk: Int,
        val targetSdk: Int
    )

    enum class Phase(val value: String) {
        DEVELOP(value = "dev"),
        ALPHA(value = "alpha"),
        BETA(value = "beta"),
        RELEASE_CANDIDATE(value = "rc"),
        RELEASE("release")
    }
}