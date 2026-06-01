package com.kevinroditi.cochiwawa.domain.usecase

import com.kevinroditi.cochiwawa.domain.model.Ride
import com.kevinroditi.cochiwawa.domain.repository.RideRepository

class GetRideByIdUseCase(private val repository: RideRepository) {
    suspend operator fun invoke(id: String): Ride? = repository.getRideById(id)
}
