package com.kevinroditi.cochiwawa.domain.usecase

import com.kevinroditi.cochiwawa.domain.model.Ride
import com.kevinroditi.cochiwawa.domain.repository.RideRepository
import kotlinx.datetime.Instant

class SearchRidesUseCase(private val repository: RideRepository) {
    suspend operator fun invoke(origin: String, destination: String, date: Instant): List<Ride> {
        return repository.searchRides(origin, destination, date)
    }
}
