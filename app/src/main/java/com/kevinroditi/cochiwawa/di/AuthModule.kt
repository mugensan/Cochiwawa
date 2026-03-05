package com.kevinroditi.cochiwawa.di

import com.kevinroditi.cochiwawa.data.repository.FakeAuthRepository
import com.kevinroditi.cochiwawa.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return FakeAuthRepository()
    }
}
