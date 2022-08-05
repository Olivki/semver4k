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

import net.ormr.semver4k.SemVerParseException.*
import net.ormr.semver4k.SemVerParser.Matcher.*

internal class SemVerParser(private val source: String) {
    internal companion object {
        private const val NULL = '\u0000'

        fun parseSemVer(source: String): Result<SemVer> {
            val parser = SemVerParser(source)
            return kotlin.runCatching { parser.semVer() }
        }

        fun parseIdentifierSequence(source: String): Result<List<Identifier>> {
            val parser = SemVerParser(source)
            return kotlin.runCatching { parser.identifierSequence(null) }
        }

        fun parseIdentifier(source: String): Result<Identifier> {
            val parser = SemVerParser(source)
            return kotlin.runCatching { parser.identifier() }
        }
    }

    private var cursorStart = 0
    private var cursor = 0

    private fun semVer(): SemVer {
        val major = versionCore("major")
        consume(DOT, "Expect '.' after major version")
        val minor = versionCore("minor")
        consume(DOT, "Expect '.' after minor version")
        val patch = versionCore("patch")
        val preRelease = if (match(HYPHEN)) identifierSequence("-") else emptyList()
        val buildMetadata = if (match(PLUS)) identifierSequence("+") else emptyList()
        consume(END_OF_INPUT, "Expected end of input")
        return SemVer(major, minor, patch, preRelease, buildMetadata)
    }

    private fun versionCore(name: String): UInt {
        syncCursor()

        consume(NUMBER, "Expected $name version")

        while (peekMatch(NUMBER)) {
            advance()
        }

        val text = getCurrentText()
        return try {
            text.toUInt()
        } catch (e: NumberFormatException) {
            throw InvalidNumber("'$text' is not a valid number.", e)
        }
    }

    private fun identifierSequence(symbol: String?): List<Identifier> = buildList {
        do {
            if (!peekMatch(NUMBER, LETTER, HYPHEN)) {
                val preMessage =
                    symbol?.let { "Expected identifier after '$it'" } ?: "Expected identifier at start of sequence"
                if (isAtEnd()) {
                    throw UnexpectedEndOfInput("$preMessage, got end of input.")
                } else {
                    throw UnexpectedCharacter("$preMessage, got '${peek()}'.")
                }
            }

            add(identifier())
        } while (match(DOT))
    }

    private fun identifier(): Identifier {
        syncCursor()

        if (isAtEnd()) {
            throw UnexpectedEndOfInput("Expected identifier, got end of input.")
        }

        while (peekMatch(NUMBER, LETTER, HYPHEN)) {
            advance()
        }

        val text = getCurrentText()
        return when {
            text.isNumber() -> Identifier.Numeric(text.toULong())
            else -> Identifier.Alphanumeric(text)
        }
    }

    private fun consume(matcher: Matcher, message: String): Char = when {
        isAtEnd() -> if (matcher == END_OF_INPUT) NULL else throw UnexpectedEndOfInput("$message, got end of input.")
        else -> {
            val char = advance()
            when {
                matcher.isMatch(char) -> char
                else -> throw UnexpectedCharacter("$message, got '$char'.")
            }
        }
    }

    private fun peekMatch(matcher: Matcher): Boolean = when {
        isAtEnd() -> matcher == END_OF_INPUT
        else -> matcher.isMatch(peek())
    }

    private fun peekMatch(vararg matchers: Matcher): Boolean = when {
        isAtEnd() -> matchers.any { it == END_OF_INPUT }
        else -> {
            val peek = peek()
            matchers.any { it.isMatch(peek) }
        }
    }

    private fun match(matcher: Matcher): Boolean = when {
        isAtEnd() -> matcher == END_OF_INPUT
        !matcher.isMatch(peek()) -> false
        else -> {
            advance()
            true
        }
    }

    private fun advance(): Char {
        cursor++
        return source[cursor - 1]
    }

    private fun peek(): Char = if (isAtEnd()) '\u0000' else source[cursor]

    private fun isAtEnd(): Boolean = cursor >= source.length

    private fun hasMore(): Boolean = !isAtEnd()

    private fun getCurrentText(): String = source.substring(cursorStart, cursor)

    private fun syncCursor() {
        cursorStart = cursor
    }

    private enum class Matcher(val symbol: String, private val matcher: (Char) -> Boolean) {
        NUMBER("[0-9]", { it in '0'..'9' }),
        LETTER("[A-Za-z]", { it in 'A'..'Z' || it in 'a'..'z' }),
        DOT(".", { it == '.' }),
        HYPHEN("-", { it == '-' }),
        PLUS("+", { it == '+' }),
        END_OF_INPUT("end of input", { it == NULL });

        fun isMatch(char: Char): Boolean = matcher(char)
    }
}