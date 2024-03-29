/*
 * Copyright 2022 Oliver Berg
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

package net.ormr.semver4k.tests

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.types.shouldBeInstanceOf
import net.ormr.semver4k.Identifier
import net.ormr.semver4k.SemVerParser

internal inline fun <reified T : Identifier> checkIdentifierCoercion(text: String) {
    Identifier(text).shouldBeInstanceOf<T>()
    shouldNotThrowAny { SemVerParser.parseIdentifier(text).getOrThrow() }.shouldBeInstanceOf<T>()
}