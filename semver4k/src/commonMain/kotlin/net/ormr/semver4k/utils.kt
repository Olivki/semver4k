/*
 * Copyright 2021-2022 Oliver Berg
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

internal fun Char.isNumber(): Boolean = this in '0'..'9'

internal fun Char.isAlpha(): Boolean = this in 'A'..'Z' || this in 'a'..'z' || this == '-'

internal fun Char.isAlphanumeric(): Boolean = isAlpha() || isNumber()

internal fun String.isNumber(): Boolean = when {
    isEmpty() -> false
    // if it doesn't start with a number it's not a numeric identifier, numeric identifiers are not allowed to
    // have  leading zeros either, so if it has a leading zero, it's not a numeric identifier either
    first() !in '1'..'9' -> false
    // we've already made sure that there is no leading zero above, so we can just check if all characters are
    // numbers here without having to worry about leading zeros
    else -> this.all { it.isNumber() }
}