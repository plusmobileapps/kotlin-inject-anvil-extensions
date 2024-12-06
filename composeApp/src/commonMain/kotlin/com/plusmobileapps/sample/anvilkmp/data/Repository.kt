package com.plusmobileapps.sample.anvilkmp.data

import com.plusmobileapps.sample.anvilkmp.di.CoroutineConstants
import com.plusmobileapps.sample.anvilkmp.di.Named
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.coroutines.CoroutineContext

interface Repository {
    suspend fun get(): String
}

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, boundType = Repository::class)
class RepositoryImpl(
    @Named(CoroutineConstants.IO) private val ioDispatcher: CoroutineContext,
) : Repository {
    override suspend fun get(): String = withContext(ioDispatcher) {
        delay(1000L)
        "Hello from RepositoryImpl"
    }
}