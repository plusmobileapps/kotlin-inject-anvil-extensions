package com.plusmobileapps.sample.anvilkmp.di

import com.plusmobileapps.sample.anvilkmp.data.Repository
import com.plusmobileapps.sample.anvilkmp.data.RepositoryImpl
import me.tatarka.inject.annotations.Provides

interface DataComponent {

    @Provides
    fun provideRepository(repositoryImpl: RepositoryImpl): Repository = repositoryImpl
}