package com.plusmobileapps.kotlin.inject.decompose.runtime

import software.amazon.lastmile.kotlin.inject.anvil.extend.ContributingAnnotation
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.reflect.KClass

/**
 * Generates a component interface for an annotated class and contributes a binding method to
 * the given [scope]. Imagine this example:
 * ```
 * interface MovieRepository
 *
 * interface MovieRepositoryFactory {
 *     fun create(id: String): MovieRepository
 * }
 *
 * @Inject
 * @SingleIn(AppScope::class)
 * class MovieRepositoryImpl(
 *      @Assisted val id: String,
 * ) : MovieRepository
 *
 * @Inject
 * @ContributesBinding(AppScope::class)
 * @SingleIn(AppScope::class)
 * class DefaultMovieRepositoryFactory(
 *      private val realFactory: (String) -> MovieRepositoryImpl,
 * ) : MovieRepositoryFactory {
 *     override fun create(id: String): MovieRepository = realFactory(id)
 * }
 * ```
 *
 * This is a lot of boilerplate any time you want to use assisted injection providing a
 * factory interface. You can eliminate the default factory class and replace it with the
 * [ContributesAssistedFactory] annotation. The equivalent would be:
 *
 * ```
 * interface MovieRepository
 *
 * interface MovieRepositoryFactory {
 *     fun create(id: String): MovieRepository
 * }
 *
 * @Inject
 * @ContributesAssistedFactory(
 *    assistedFactory = MovieRepositoryFactory::class,
 * )
 * class MovieRepositoryImpl(
 *      @Assisted val id: String,
 * ) : MovieRepository
 * ```
 */
@Target(CLASS)
@Repeatable
@ContributingAnnotation
public annotation class ContributesAssistedFactory(
    /**
     * The assisted factory that will be used to create instances of the bound type.
     */
    val assistedFactory: KClass<*>,
)