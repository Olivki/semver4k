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

import kotlinx.collections.immutable.PersistentList

public sealed class Identifier : Comparable<Identifier> {
    public companion object {
        private val IDENTIFIER_CHARACTERS: Regex = "[0-9A-Za-z-]+".toRegex()

        /**
         * Returns the result of parsing the given [text] to a identifier sequence.
         *
         * An identifier sequence is a sequence of identifiers, separated by a `.` between each identifier.
         */
        public fun parseSequence(text: String): Result<PersistentList<Identifier>> =
            SemVerParser.parseIdentifierSequence(text)

        /**
         * Returns the result of parsing the given [text] to an [Identifier].
         *
         * If the parsing was successful, then the type of the returned identifier depends on the contents of `text`.
         * If `text` only contains numbers *(without a leading zero)* then [numeric identifier][Numeric] is returned,
         * otherwise [alphanumeric identifier][Alphanumeric] is returned.
         */
        public fun parse(text: String): Result<Identifier> = SemVerParser.parseIdentifier(text)

        /**
         * Returns a new [numeric identifier][Numeric] containing the given [value].
         */
        public fun of(value: ULong): Identifier = Numeric(value)

        /**
         * Returns a new identifier containing the given [text].
         *
         * The type of the returned identifier depends on the contents of `text`. If `text` only contains numbers
         * *(without a leading zero)* then [numeric identifier][Numeric] is returned, otherwise
         * [alphanumeric identifier][Alphanumeric] is returned.
         *
         * @throws IllegalArgumentException if [text] is empty or contains illegal characters
         *
         * @see parse
         */
        public fun of(text: String): Identifier {
            require(text.isNotEmpty()) { "Identifier should not be empty." }
            require(text.matches(IDENTIFIER_CHARACTERS)) { "Identifier contains illegal characters; '$text'." }
            return if (text.isNumber()) Numeric(text.toULong()) else Alphanumeric(text)
        }
    }

    override fun compareTo(other: Identifier): Int = when (this) {
        is Numeric -> when (other) {
            is Numeric -> value.compareTo(other.value)
            is Alphanumeric -> -1 // "Numeric identifiers always have lower precedence than non-numeric identifiers."
        }
        is Alphanumeric -> when (other) {
            is Numeric -> 1 // "Numeric identifiers always have lower precedence than non-numeric identifiers."
            is Alphanumeric -> text.compareTo(other.text)
        }
    }

    /**
     * An identifier that contains only numbers.
     */
    public class Numeric internal constructor(public val value: ULong) : Identifier() {
        override fun equals(other: Any?): Boolean = when {
            this === other -> true
            other !is Numeric -> false
            value != other.value -> false
            else -> true
        }

        override fun hashCode(): Int = value.hashCode()

        override fun toString(): String = value.toString()
    }

    /**
     * An identifier that can contain numbers, ASCII characters, and hyphens *(`[0-9A-Za-z-]`)*.
     */
    public class Alphanumeric internal constructor(public val text: String) : Identifier() {
        override fun equals(other: Any?): Boolean = when {
            this === other -> true
            other !is Alphanumeric -> false
            text != other.text -> false
            else -> true
        }

        override fun hashCode(): Int = text.hashCode()

        override fun toString(): String = text
    }
}