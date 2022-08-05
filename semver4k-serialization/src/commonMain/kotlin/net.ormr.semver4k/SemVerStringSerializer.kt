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

package net.ormr.semver4k

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A [KSerializer] that serializes [SemVer] instances to a [String].
 *
 * The serializer uses [SemVer.parse] and [SemVer.toString] for conversion.
 */
public object SemVerStringSerializer : KSerializer<SemVer> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("net.ormr.semver4k.SemVer", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): SemVer {
        val text = decoder.decodeString()
        return SemVer.parse(text).getOrElse { throw SerializationException("Invalid semantic version: $text", it) }
    }

    override fun serialize(encoder: Encoder, value: SemVer) {
        encoder.encodeString(value.toString())
    }
}