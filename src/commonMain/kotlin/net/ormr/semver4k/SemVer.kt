/*
 * Copyright 2021 Oliver Berg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ormr.semver4k

/**
 * Represents a semantic version, as defined in the [Semantic Versioning 2.0 Specification](https://semver.org/spec/v2.0.0.html).
 *
 * Note that while [compareTo] may return `0` for one instance, that is not a guarantee that [equals] will return
 * `true` for that same instance. This is because `compareTo` ignores the [buildMetadata], while `equals` does not
 * ignore it.
 *
 * @property major The major version of this semantic version.
 * @property minor The minor version of this semantic version.
 * @property patch The patch version of this semantic version.
 * @property preRelease The pre-release of this semantic version.
 *
 * If this is empty, then that means that this semantic version is not a pre-release version.
 * @property buildMetadata The build metadata of this semantic version.
 *
 * If this is empty, then that means that this semantic version does not define any build metadata.
 */
public data class SemVer(
    public val major: UInt,
    public val minor: UInt,
    public val patch: UInt,
    public val preRelease: List<Identifier> = emptyList(),
    public val buildMetadata: List<Identifier> = emptyList(),
) : Comparable<SemVer> {
    public companion object {
        /**
         * Returns the result of parsing the given [text] to a `SemVer` instance.
         */
        public fun parse(text: String): Result<SemVer> = SemVerParser.parseSemVer(text)

        /**
         * Returns a new `SemVer` instance.
         *
         * @param major the major version
         * @param minor the minor version
         * @param patch the patch version
         * @param preRelease the pre-release to parse, or `null` if the version is not a pre-release
         * @param buildMetadata the build metadata to parse, or `null` if the version has no build metadata
         *
         * @throws IllegalArgumentException if either [preRelease] or [buildMetadata] are invalid
         */
        public fun of(
            major: UInt,
            minor: UInt,
            patch: UInt,
            preRelease: String? = null,
            buildMetadata: String? = null,
        ): SemVer = SemVer(
            major,
            minor,
            patch,
            preRelease.toIdentifierSequence("pre-release"),
            buildMetadata.toIdentifierSequence("build metadata"),
        )

        private fun String?.toIdentifierSequence(name: String): List<Identifier> = this?.let {
            SemVerParser.parseIdentifierSequence(it)
                .getOrElse { e -> throw IllegalArgumentException("Invalid $name '$it'.", e) }
        } ?: emptyList()
    }

    /**
     * Returns `true` if this represents a version for something that's currently in its initial development phase,
     * otherwise `false`.
     *
     * If something is in its initial development phase, then it should not be considered stable, and anything may
     * change at any time.
     */
    public val isInitialDevelopmentPhase: Boolean
        get() = major == 0u

    /**
     * Returns `true` if this version is a pre-release version, otherwise `false`.
     *
     * Whether a version is considered a pre-release depends on whether [preRelease] is empty or not.
     */
    public val isPreRelease: Boolean
        get() = preRelease.isNotEmpty()

    /**
     * Returns `true` if this version has build metadata defined, otherwise `false`.
     */
    public val hasBuildMetadata: Boolean
        get() = buildMetadata.isNotEmpty()

    override fun compareTo(other: SemVer): Int = when {
        major > other.major -> 1
        major < other.major -> -1
        minor > other.minor -> 1
        minor < other.minor -> -1
        patch > other.patch -> 1
        patch < other.patch -> -1
        preRelease.isEmpty() && other.preRelease.isNotEmpty() -> 1
        preRelease.isEmpty() && other.preRelease.isEmpty() -> 0
        preRelease.isNotEmpty() && other.preRelease.isEmpty() -> -1
        else -> when (val result = comparePreReleases(other)) {
            0 -> preRelease.size.compareTo(other.preRelease.size)
            else -> result
        }
    }

    private fun comparePreReleases(other: SemVer): Int {
        // we need to have this as a separate variable, if we inline it then the Kotlin/JS compiler will fail
        val identifiers = preRelease.asSequence() zip other.preRelease.asSequence()
        for ((left, right) in identifiers) {
            return left.compareTo(right).takeIf { it != 0 } ?: continue
        }
        return 0
    }

    override fun toString(): String = buildString {
        append(major)
        append('.')
        append(minor)
        append('.')
        append(patch)
        if (preRelease.isNotEmpty()) append('-')
        preRelease.joinTo(this, separator = ".")
        if (buildMetadata.isNotEmpty()) append('+')
        buildMetadata.joinTo(this, separator = ".")
    }
}