package com.plusmobileapps.sample.anvilkmp.di

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@MergeComponent(AppScope::class)
@SingleIn(AppScope::class)
interface AppComponent {
    abstract val repository: Repository
}
interface Repository {
    fun get(): String
}

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, boundType = Repository::class)
class RepositoryImpl : Repository {
    override fun get(): String {
        return "Hello from RepositoryImpl"
    }
}