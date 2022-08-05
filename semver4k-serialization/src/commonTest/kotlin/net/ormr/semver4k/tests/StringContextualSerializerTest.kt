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

import io.kotest.assertions.throwables.shouldNotThrowUnit
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import net.ormr.semver4k.SemVer
import net.ormr.semver4k.installSemVerStringSerializer
import kotlin.test.Test

class StringContextualSerializerTest {
    private companion object {
        private const val ITERATIONS = 200
    }

    private val json = Json {
        serializersModule = SerializersModule {
            installSemVerStringSerializer()
        }
    }

    @Test
    fun serializerShouldBeAbleToFindContextualSemVerSerializer() = runTest {
        checkSemVer(ITERATIONS) { version ->
            shouldNotThrowUnit<SerializationException> {
                val serializedOutput = json.encodeToString(Carrier.serializer(), Carrier(version))
                val deserializedInput = json.decodeFromString(Carrier.serializer(), serializedOutput)
                deserializedInput.version shouldBe version
            }
        }
    }

    @Serializable
    private data class Carrier(
        @Contextual val version: SemVer,
    )
}