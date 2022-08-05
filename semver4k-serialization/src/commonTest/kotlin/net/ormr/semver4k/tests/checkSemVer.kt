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

import io.kotest.property.Arb
import io.kotest.property.PropertyContext
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.uInt
import io.kotest.property.checkAll
import net.ormr.semver4k.SemVer

internal suspend inline fun checkSemVer(
    iterations: Int,
    maxParts: Int = 5,
    crossinline block: PropertyContext.(SemVer) -> Unit,
) {
    checkAll(
        iterations,
        Arb.uInt(),
        Arb.uInt(),
        Arb.uInt(),
        Arb.semVerIdentifier(maxParts = maxParts).orNull(0.5),
        Arb.semVerIdentifier(maxParts = maxParts).orNull(0.5),
    ) { major, minor, patch, preRelease, buildMetadata ->
        block(SemVer(major, minor, patch, preRelease?.first ?: emptyList(), buildMetadata?.first ?: emptyList()))
    }
}