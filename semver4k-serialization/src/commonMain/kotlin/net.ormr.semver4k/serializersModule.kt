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

import kotlinx.serialization.modules.SerializersModuleBuilder

/**
 * Installs a [contextual][SerializersModuleBuilder.contextual] module entry to serialize [SemVer] instances using
 * the [SemVerStringSerializer] serializer.
 */
public fun SerializersModuleBuilder.installSemVerStringSerializer() {
    contextual(SemVer::class, SemVerStringSerializer)
}