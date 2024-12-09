package com.plusmobileapps.sample.anvilkmp.data

import com.plusmobileapps.sample.anvilkmp.di.IO
import com.plusmobileapps.sample.anvilkmp.di.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import kotlin.coroutines.CoroutineContext

interface Repository {
    suspend fun get(): String
}

@Inject
@Singleton
class RepositoryImpl(
    @IO private val ioDispatcher: CoroutineContext,
) : Repository {
    override suspend fun get(): String = withContext(ioDispatcher) {
        delay(1000L)
        "Hello from RepositoryImpl"
    }
}