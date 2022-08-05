## semver4k

![Maven Central](https://img.shields.io/maven-central/v/net.ormr.semver4k/semver4k?label=RELEASE&style=for-the-badge)

A no dependencies, Kotlin multiplatform implementation of the semantic versioning 2.0 specification. 

Supports building semantic versions directly, and parsing valid semantic versions.

## Installation

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("net.ormr.semver4k:semver4k:${RELEASE_VERSION}")
    // if you want kotlinx.serialization support
    implementation("net.ormr.semver4k:semver4k-serialization:${RELEASE_VERSION}")
}
```

## Serialization
If serialization support for `SemVer` instances is desired, include the `semver4k-serialization` dependency as shown in the [Installation](#installation) section.

Then either annotate the desired `SemVer` properties with `@Serializable(with = SemVerStringSerializer::class)` or just replace the `SemVer` type with the `SerializableSemVer` type. *(`SerializableSemVer` is just a type-alias, not its own concrete type)*

An extension function for adding a contextual serializer in a `SerializersModuleBuilder` instance is also provided, named `installSemVerStringSerializer`.

The `kotlinx.serialization` support was split into its own module for the sake of keeping the core library small and light-weight without any external dependencies. 

## Usage

### Building a `SemVer` instance

#### Via the `SemVer` factory function

```kotlin
SemVer(1u, 0u, 0u, preRelease = "alpha.1", buildMetadata = "a474fh6")
```

#### Invoking the constructor directly

```kotlin
SemVer(
    1u, 
    0u, 
    0u, 
    preRelease = listOf(Identifier("alpha"), Identifier(1u)),
    buildMetadata = listOf(Identifier("a474fh6")),
)
```

In both cases `preRelease` and `buildMetadata` are optional, and can be omitted if not needed.

### Parsing a `SemVer` instance

```kotlin
SemVer.parse("1.0.0-alpha.1+a474fh6")
```

The above string will be parsed into an instance that is structurally the same as the instances created above.

Note that `parse` does not return a `SemVer` instance, but rather a [`Result<SemVer>`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/) and needs to be handled appropriately.

### Comparing two `SemVer` instances

`SemVer` implements the `Comparable` interface, so all operators and functions that work with that will work with the `SemVer` instance, i.e: `< > <= >=`.

```kotlin
SemVer(1u, 0u, 0u) > SemVer(0u, 10u, 2u) // true
SemVer(1u, 0u, 0u) < SemVer(0u, 10u, 2u) // false
// etc...
```

### Modifying a `SemVer` instance

`SemVer` instances are not mutable.

Therefore it is not possible to just modify an existing instance, and instead the `copy` function should be used to create an updated instance.

There is currently no convenience functions for operations like just incrementing the major, minor or patch number, there may be some added in the future.

## Info
Please note that this library is made with Kotlin exclusive use in mind, and therefore uses [unsigned integers](https://kotlinlang.org/docs/basic-types.html#unsigned-integers) to represent the major, minor and patch versions. This may result in weird interop if `SemVer` instances are used in a shared Java/JS code base.

The only thing to look out for is that the equality and comparability of two `SemVer` instances are *not* guaranteed to be the same. Comparability of two instances ignores the build metadata, as per the specification, but equality checks includes the build metadata. This means that two instances which may be equal according to `compareTo` may not actually be equal according to the equality check. This may or may not change in the future.