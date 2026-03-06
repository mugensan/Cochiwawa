package com.kevinroditi.cochiwawa.di

import com.cochiwawa.shared.NetworkClient
import com.kevinroditi.cochiwawa.data.repository.FakeBookingRepository
import com.kevinroditi.cochiwawa.data.repository.RestRideRepository
import com.kevinroditi.cochiwawa.domain.repository.BookingRepository
import com.kevinroditi.cochiwawa.domain.repository.RideRepository
import com.kevinroditi.cochiwawa.domain.usecase.BookSeatUseCase
import com.kevinroditi.cochiwawa.domain.usecase.GetRideByIdUseCase
import com.kevinroditi.cochiwawa.domain.usecase.SearchRidesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNetworkClient(): NetworkClient {
        return NetworkClient()
    }

    @Provides
    @Singleton
    fun provideRideRepository(networkClient: NetworkClient): RideRepository {
        return RestRideRepository(networkClient)
    }

    @Provides
    @Singleton
    fun provideBookingRepository(rideRepository: RideRepository): BookingRepository {
        return FakeBookingRepository(rideRepository)
    }

    @Provides
    @Singleton
    fun provideSearchRidesUseCase(repository: RideRepository): SearchRidesUseCase {
        return SearchRidesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetRideByIdUseCase(repository: RideRepository): GetRideByIdUseCase {
        return GetRideByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideBookSeatUseCase(repository: BookingRepository): BookSeatUseCase {
        return BookSeatUseCase(repository)
    }
}
