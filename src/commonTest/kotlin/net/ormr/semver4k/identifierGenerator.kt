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

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

internal fun Arb.Companion.semVerIdentifier(minParts: Int = 1, maxParts: Int): Arb<Pair<PersistentList<Identifier>, String>> =
    object : Arb<Pair<PersistentList<Identifier>, String>>() {
        override fun edgecase(rs: RandomSource): Pair<PersistentList<Identifier>, String>? = null

        override fun sample(rs: RandomSource): Sample<Pair<PersistentList<Identifier>, String>> {
            // I don't really know what a sensible maxSize would be, even 100 feels a bit large for a semver identifier
            // the system should be able to handle identifiers of any size (as long as the final string isn't larger
            // than what is supported by the JVM)
            val stringArb = Arb.string(minSize = 1, maxSize = 100, codepoints = Codepoint.semVerIdentifier())
            val sequenceSize = rs.random.nextInt(1, maxParts + 1)
            val identifiers = stringArb.samples(rs)
                .take(sequenceSize)
                .map { it.value }
                .map { Identifier.of(it) }
                .toPersistentList()
            val identifierTextSequence = identifiers.joinToString(separator = ".")
            return Sample(identifiers to identifierTextSequence)
        }
    }

internal fun Codepoint.Companion.semVerIdentifier(): Arb<Codepoint> =
    Arb.element((('a'..'z') + ('A'..'Z') + ('0'..'9') + '-').toList()).map { Codepoint(it.code) }