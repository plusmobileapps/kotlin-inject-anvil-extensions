# Assisted Factory Extension

An extension to help bind factory interfaces in anvil when using [assisted injection from kotlin-inject](https://github.com/evant/kotlin-inject?tab=readme-ov-file#function-support--assisted-injection). 

## Setup 

The library is available through maven central, so include this in your repositories of your `settings.gradle.kts`. 

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
```

* compiler - [![Maven Central](https://img.shields.io/maven-central/v/com.plusmobileapps.kotlin-inject-anvil-extensions/assisted-factory-compiler?color=blue)](https://central.sonatype.com/artifact/com.plusmobileapps.kotlin-inject-anvil-extensions/assisted-factory-compiler)
* runtime - [![Maven Central](https://img.shields.io/maven-central/v/com.plusmobileapps.kotlin-inject-anvil-extensions/assisted-factory-runtime?color=blue)](https://central.sonatype.com/artifact/com.plusmobileapps.kotlin-inject-anvil-extensions/assisted-factory-runtime)

When using version catalogs, add the following version and library modules.

```toml
[versions]
kotlinInjectAnvilExtensions = "{version}"

[libraries]
kotlinInjectAnvilExtensions-assistedFactory-compiler = { module = "com.plusmobileapps.kotlin-inject-anvil-extensions:assisted-factory-compiler", version.ref = "kotlinInjectAnvilExtensions" }
kotlinInjectAnvilExtensions-assistedFactory-runtime = { module = "com.plusmobileapps.kotlin-inject-anvil-extensions:assisted-factory-runtime", version.ref = "kotlinInjectAnvilExtensions" }
```

Then in the `build.gradle.kts` configure ksp with the libraries.

```kotlin
dependencies {
    // update with your app's targets.
    val targets = listOf(
        "kspAndroid",
        "kspIosX64",
        "kspIosArm64",
        "kspIosSimulatorArm64"
    )
    commonMainImplementation(libs.kotlinInjectAnvilExtensions.assistedFactory.runtime)
    targets.forEach {
        add(it, libs.kotlinInjectAnvilExtensions.assistedFactory.compiler)
    }
}
```

## Why Assisted Factory? 

The current APIs between kotlin-inject and kotlin-inject-anvil do allow you to use assisted injection, however to bind a factory interface in your dependency graph to generate an assisted dependency can require a bit of boiler plate. For this example let's assume the following dependency: 

```kotlin
interface MovieRepository {
    fun get(): Movie
}

@Inject
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class RealMovieRepository(@Assisted val id: String) : MovieRepository {
    override fun get(): Moview = TODO()
}
```

The above code will bind a factory method `(String) -> RealMovieRepository` that can be injected in any other dependency. Although for testing purposes, it would be better if the factory method bound was returning the interface instead like `(String) -> MovieRepository`. An even better solution would be to create a strongly typed interface and bind that to the real factory method, however this requires some boiler plate creating a real factory that injects the real factory and implementing the factory interface.

```kotlin
interface MovieRepository {
    interface Factory {
        fun create(id: String): MovieRepository
    }
}

@Inject
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class RealMovieRepositoryFactory(
    val realFactory: (String) -> RealMovieRepository,
) : MovieRepository.Factory {

    override fun create(id: String): MovieRepository = realFactory(id)
}
```

This is where the assisted factory extension comes in by implementing that boiler plate for you removing the need to implement a real factory binding. 

## Usage

To use the assisted factory anvil extension, all that is needed is to annotate the dependency with the `@ContributesAssistedFactory` annotation.

```kotlin
interface MovieRepository {
    fun get(): Movie

    interface Factory {
        fun create(id: String): MovieRepository
    }
}

@Inject
@ContributesAssistedFactory(
    scope = AppScope::class,
    assistedFactory = MovieRepository.Factory::class,
)
class RealMovieRepository(
    @Assisted val id: String,
) : MovieRepository {
    override fun get(): Moview = TODO()
}
```

The above code will generate a real factory and bind the factory interface in the dependency graph for you. Then you can simply inject that factory into any other class.

```kotlin
@Inject
class MovieDetailViewModel(
    private val factory: MovieRepository.Factory
) {
    fun create(id: String) {
        val repository: MovieRepository = factory.create(id)
    }
}
```