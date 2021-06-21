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

import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.comparables.shouldNotBeEqualComparingTo
import io.kotest.matchers.shouldNotBe
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.Test

class SemVerPrecedenceTest {
    @Test
    fun preReleaseVersionShouldHaveLowerPrecedenceThanOneWithout() {
        val a = SemVer(1u, 0u, 0u)
        val b = a.copy(preRelease = persistentListOf(Identifier.Alphanumeric("beta")))
        a shouldBeGreaterThan b
        b shouldBeLessThan a
    }

    @Test
    fun preReleasePrecedenceTests() = runTest {
        forAll(
            row(SemVer(1u, 0u, 0u), SemVer.of(1u, 0u, 0u, "rc")),
            row(SemVer.of(1u, 0u, 0u, "rc"), SemVer.of(1u, 0u, 0u, "beta.11")),
            row(SemVer.of(1u, 0u, 0u, "beta.11"), SemVer.of(1u, 0u, 0u, "beta.2")),
            row(SemVer.of(1u, 0u, 0u, "beta.2"), SemVer.of(1u, 0u, 0u, "beta")),
            row(SemVer.of(1u, 0u, 0u, "beta"), SemVer.of(1u, 0u, 0u, "alpha.beta")),
            row(SemVer.of(1u, 0u, 0u, "alpha.beta"), SemVer.of(1u, 0u, 0u, "alpha.1")),
            row(SemVer.of(1u, 0u, 0u, "alpha.1"), SemVer.of(1u, 0u, 0u, "alpha")),
        ) { a, b ->
            a shouldBeGreaterThan b
            a shouldNotBeEqualComparingTo b
            b shouldBeLessThan a
        }
    }

    @Test
    fun basicVersionPrecedenceTests() = runTest {
        forAll(
            row(SemVer(2u, 1u, 1u), SemVer(2u, 1u, 0u)),
            row(SemVer(2u, 1u, 0u), SemVer(2u, 0u, 0u)),
            row(SemVer(2u, 0u, 0u), SemVer(1u, 0u, 0u)),
        ) { a, b ->
            a shouldBeGreaterThan b
            a shouldNotBeEqualComparingTo b
            b shouldBeLessThan a
        }
    }

    @Test
    fun buildMetadataShouldBeIgnoredForPrecedence() {
        val a = SemVer(1u, 0u, 0u)
        val b = a.copy(buildMetadata = persistentListOf(Identifier.Alphanumeric("some-data")))
        a shouldBeEqualComparingTo b
    }

    @Test
    fun buildMetadataShouldBeIncludedForEquals() {
        val a = SemVer(1u, 0u, 0u)
        val b = a.copy(buildMetadata = persistentListOf(Identifier.Alphanumeric("some-data")))
        a shouldNotBe b
    }
}