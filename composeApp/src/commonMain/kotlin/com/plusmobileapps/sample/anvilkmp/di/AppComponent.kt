package com.plusmobileapps.sample.anvilkmp.di

import com.plusmobileapps.sample.anvilkmp.di.CoroutineConstants.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.coroutines.CoroutineContext

@MergeComponent(AppScope::class)
@SingleIn(AppScope::class)
interface AppComponent {
    abstract val repository: Repository
}
interface Repository {
    suspend fun get(): String
}

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, boundType = Repository::class)
class RepositoryImpl(
    @Named(IO) private val ioDispatcher: CoroutineContext,
) : Repository {
    override suspend fun get(): String = withContext(ioDispatcher) {
        delay(1000L)
        "Hello from RepositoryImpl"
    }
}