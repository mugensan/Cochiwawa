package com.kevinroditi.cochiwawa.di

import com.cochiwawa.shared.GraphQLClient
import com.kevinroditi.cochiwawa.data.local.AuthPreferences
import com.kevinroditi.cochiwawa.data.repository.AuthRepositoryImpl
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
    fun provideAuthRepository(
        client: GraphQLClient,
        authPreferences: AuthPreferences
    ): AuthRepository {
        return AuthRepositoryImpl(client, authPreferences)
    }
}
