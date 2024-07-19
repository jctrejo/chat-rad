package com.chat.radar.di

import com.chat.radar.domain.remote.Service
import com.chat.radar.domain.repository.RepositoryImpl
import com.chat.radar.domain.remote.network.NetworkClient
import com.chat.radar.domain.repository.Repository
import com.chat.radar.domain.usecase.ExampleCase
import com.chat.radar.domain.usecase.impl.ExampleCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)

object Module {
    
    @Provides
    fun getServices(): Service {
        return NetworkClient().getService()
    }

    @Provides
    fun getRepository(service: Service): Repository {
        return RepositoryImpl(service)
    }

    @Provides
    fun getExample(repository: Repository): ExampleCase {
        return ExampleCaseImpl(repository)
    }
}
