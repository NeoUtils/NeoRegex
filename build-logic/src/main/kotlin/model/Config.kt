/*
 * NeoRegex.
 *
 * Copyright (C) 2024 Irineu A. Silva.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package model

data class Config(
    val name: String,
    val version: Version,
    val android: Android,
    val basePackage: String
) {

    fun distName(): String {
        return "$name-${version.name()}"
    }

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