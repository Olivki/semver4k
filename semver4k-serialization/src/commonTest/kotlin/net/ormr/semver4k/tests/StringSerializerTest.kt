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
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.ormr.semver4k.SemVer
import net.ormr.semver4k.SemVerStringSerializer
import kotlin.test.Test

class StringSerializerTest {
    private companion object {
        private const val ITERATIONS = 200
    }

    private val json = Json

    @Test
    fun serializeShouldProduceSameOutputAsToString() = runTest {
        checkSemVer(ITERATIONS) { version ->
            json.encodeToString(SemVerStringSerializer, version).shrink(1) shouldBe version.toString()
        }
    }

    @Test
    fun deserializeShouldProduceEquivalentInstance() = runTest {
        checkSemVer(ITERATIONS) { version ->
            val serializedOutput = json.encodeToString(SemVerStringSerializer, version)
            val deserializedVersion =
                shouldNotThrowAny { json.decodeFromString(SemVerStringSerializer, serializedOutput) }
            deserializedVersion shouldBe version
        }
    }

    @Test
    fun testStructureSerialization() = runTest {
        checkSemVer(ITERATIONS) { version ->
            val serializedOutput = json.encodeToString(Carrier.serializer(), Carrier(version))
            serializedOutput shouldBe """{"version":"$version"}"""
        }
    }

    @Serializable
    private data class Carrier(
        @Serializable(with = SemVerStringSerializer::class)
        val version: SemVer,
    )
}