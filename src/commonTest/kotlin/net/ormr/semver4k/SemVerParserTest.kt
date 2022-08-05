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

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.comparables.shouldNotBeEqualComparingTo
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.uInt
import io.kotest.property.checkAll
import kotlin.test.Test

class SemVerParserTest {
    private companion object {
        private const val ITERATIONS = 200
    }

    @Test
    fun shouldParseVersionWithoutPreReleaseOrBuildMetadata() = runTest {
        checkAll<UInt, UInt, UInt>(ITERATIONS) { major, minor, patch ->
            SemVerParser.parseSemVer("$major.$minor.$patch") shouldBeSuccess SemVer(major, minor, patch)
        }
    }

    @Test
    fun shouldParseIdentifier() = runTest {
        checkAll(ITERATIONS, Arb.semVerIdentifier(maxParts = 1)) { (list, text) ->
            val expectedIdentifier = list.first()
            val parsedIdentifier = SemVerParser.parseIdentifier(text)
            val ofIdentifier = shouldNotThrowAny { Identifier.of(text) }
            parsedIdentifier shouldBeSuccess expectedIdentifier
            ofIdentifier shouldBe expectedIdentifier
        }
    }

    @Test
    fun shouldParseIdentifierSequence() = runTest {
        checkAll(ITERATIONS, Arb.semVerIdentifier(minParts = 2, maxParts = 10)) { (list, text) ->
            SemVerParser.parseIdentifierSequence(text).shouldBeSuccess {
                it shouldContainExactly list
            }
        }
    }

    @Test
    fun shouldParseVersionWithPreRelease() = runTest {
        checkAll(
            ITERATIONS,
            Arb.uInt(),
            Arb.uInt(),
            Arb.uInt(),
            Arb.semVerIdentifier(maxParts = 5),
        ) { major, minor, patch, (preList, preText) ->
            val expectedVersion = SemVer(major, minor, patch, preRelease = preList)
            val parsedVersion = SemVerParser.parseSemVer("$major.$minor.$patch-$preText")
            parsedVersion shouldBeSuccess expectedVersion
        }
    }

    @Test
    fun shouldParseVersionWithBuildMetadata() = runTest {
        checkAll(
            ITERATIONS,
            Arb.uInt(),
            Arb.uInt(),
            Arb.uInt(),
            Arb.semVerIdentifier(maxParts = 5),
        ) { major, minor, patch, (buildList, buildText) ->
            val expectedVersion = SemVer(major, minor, patch, buildMetadata = buildList)
            val parsedVersion = SemVerParser.parseSemVer("$major.$minor.$patch+$buildText")
            parsedVersion shouldBeSuccess expectedVersion
        }
    }

    @Test
    fun shouldParseVersionWithPreReleaseAndBuildMetadata() = runTest {
        checkAll(
            ITERATIONS,
            Arb.uInt(),
            Arb.uInt(),
            Arb.uInt(),
            Arb.semVerIdentifier(maxParts = 5),
            Arb.semVerIdentifier(maxParts = 5),
        ) { major, minor, patch, (preList, preText), (buildList, buildText) ->
            val expectedVersion = SemVer(major, minor, patch, preRelease = preList, buildMetadata = buildList)
            val parsedVersion = SemVerParser.parseSemVer("$major.$minor.$patch-$preText+$buildText")
            parsedVersion shouldBeSuccess expectedVersion
        }
    }

    @Test
    fun versionWithPreReleaseShouldAllowHyphen() {
        val preRelease = listOf(Identifier.Alphanumeric("beta-1"))
        val concreteVersion = SemVer(1u, 0u, 0u, preRelease)
        val parsedVersion = SemVer.parse("1.0.0-beta-1")
        parsedVersion shouldBeSuccess concreteVersion
        val result = shouldNotThrowAny { parsedVersion.getOrThrow() }
        result.preRelease shouldContainExactly preRelease
    }

    @Test
    fun identifierShouldBeCoercedToCorrectImplementation() = runTest {
        // if the identifier starts with a non-zero number, and only contains numbers thereafter, it should be coerced
        // into a numeric instance
        checkIdentifierCoercion<Identifier.Numeric>("69")
        // if the text then starts with a number, but contains letters afterwards, it should be coerced into an
        // alphanumeric instance
        checkIdentifierCoercion<Identifier.Alphanumeric>("69leet")
        // numeric identifiers are not allowed to be zero padded, therefore this should be coerced into an alphanumeric
        // instance
        checkIdentifierCoercion<Identifier.Alphanumeric>("069")
        // an identifier text that only contains letters should only ever be coerced into a alphanumeric instance
        checkIdentifierCoercion<Identifier.Alphanumeric>("leet")

        checkAll<ULong>(10) { value ->
            // if an unsigned number is directly passed in, then it should *always* return a numeric instance
            Identifier.of(value).shouldBeInstanceOf<Identifier.Numeric>()
        }
    }

    @Test
    fun emptyIdentifierShouldFail() {
        val ofException = shouldThrow<IllegalArgumentException> { Identifier.of("") }
        ofException shouldHaveMessage "Identifier should not be empty."

        val parsedIdentifier = Identifier.parse("")
        parsedIdentifier.shouldBeFailure {
            it.shouldBeInstanceOf<SemVerParseException.UnexpectedEndOfInput>()
            it shouldHaveMessage "Expected identifier, got end of input."
        }
    }

    @Test
    fun numericIdentifiersShouldBeCorrect() {
        val a = shouldNotThrowAny { Identifier.of("1") }
        val b = shouldNotThrowAny { Identifier.of("2") }
        a.shouldBeInstanceOf<Identifier.Numeric>()
        b.shouldBeInstanceOf<Identifier.Numeric>()

        a shouldBeLessThan b
        a shouldNotBeEqualComparingTo b
        b shouldBeGreaterThan a
    }

    @Test
    fun emptyIdentifierSequenceShouldFail() {
        val seq = SemVerParser.parseIdentifierSequence("alpha..69")
        seq.shouldBeFailure {
            it.shouldBeInstanceOf<SemVerParseException>()
            it shouldHaveMessage "Expected identifier at start of sequence, got '.'."
        }
    }
}