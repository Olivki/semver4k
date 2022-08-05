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
 * Represents an exception thrown when something goes wrong with parsing text into a [SemVer] instance.
 */
public sealed class SemVerParseException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    public class UnexpectedEndOfInput internal constructor(message: String) : SemVerParseException(message)

    public class UnexpectedCharacter internal constructor(message: String) : SemVerParseException(message)

    public class InvalidNumber internal constructor(message: String, cause: Throwable) :
        SemVerParseException(message, cause)
}