package com.kevinroditi.cochiwawa.domain.repository

interface VehicleRepository {
    suspend fun registerVehicle(
        driverId: String,
        licenseNumber: String,
        plate: String,
        model: String,
        color: String,
        insuranceUrl: String,
        photoFront: String,
        photoBack: String,
        photoLeft: String,
        photoDriverSeat: String
    ): Result<Boolean>
    
    suspend fun uploadImage(imageUri: String): Result<String>
}
