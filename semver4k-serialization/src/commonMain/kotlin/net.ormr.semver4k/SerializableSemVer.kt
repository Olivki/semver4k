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

import kotlinx.serialization.Serializable

/**
 * A [SemVer] instance that can be serialized using the [SemVerStringSerializer] serializer.
 *
 * Using this type instead of `SemVer` allows one to avoid having to manually tag a property with [Serializable].
 *
 * For example, the two following code blocks are equivalent:
 *
 * ```kotlin
 *  @Serializable
 *  class Thing(
 *      val version: SerializableSemVer,
 *  )
 * ```
 * vs
 * ```kotlin
 *  @Serializable
 *  class Thing(
 *      @Serializable(with = SemVerStringSerializer::class)
 *      val version: SemVer,
 *  )
 * ```
 */
public typealias SerializableSemVer = @Serializable(with = SemVerStringSerializer::class) SemVer