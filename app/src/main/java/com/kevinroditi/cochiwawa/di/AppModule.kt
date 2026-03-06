package com.kevinroditi.cochiwawa.di

import android.content.Context
import com.kevinroditi.cochiwawa.data.remote.AuthApi
import com.kevinroditi.cochiwawa.data.remote.VehicleApi
import com.kevinroditi.cochiwawa.data.repository.AuthRepositoryImpl
import com.kevinroditi.cochiwawa.data.repository.BookingRepositoryImpl
import com.kevinroditi.cochiwawa.data.repository.RideRepositoryImpl
import com.kevinroditi.cochiwawa.data.repository.VehicleRepositoryImpl
import com.kevinroditi.cochiwawa.domain.repository.AuthRepository
import com.kevinroditi.cochiwawa.domain.repository.BookingRepository
import com.kevinroditi.cochiwawa.domain.repository.RideRepository
import com.kevinroditi.cochiwawa.domain.repository.VehicleRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRideRepository(api: AuthApi): RideRepository {
        return RideRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideBookingRepository(api: AuthApi): BookingRepository {
        return BookingRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideVehicleRepository(api: VehicleApi): VehicleRepository {
        return VehicleRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideVehicleApi(retrofit: Retrofit): VehicleApi {
        return retrofit.create(VehicleApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }
}
