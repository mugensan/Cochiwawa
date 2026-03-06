package com.kevinroditi.cochiwawa.data.repository

import com.kevinroditi.cochiwawa.data.remote.VehicleApi
import com.kevinroditi.cochiwawa.data.remote.graphql.GraphQLRequest
import com.kevinroditi.cochiwawa.domain.repository.VehicleRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class VehicleRepositoryImpl @Inject constructor(
    private val api: VehicleApi
) : VehicleRepository {

    override suspend fun registerVehicle(
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
    ): Result<Boolean> {
        val query = """
            mutation {
                registerVehicle(
                    driverId: "$driverId",
                    driverLicenseNumber: "$licenseNumber",
                    vehiclePlate: "$plate",
                    vehicleModel: "$model",
                    vehicleColor: "$color",
                    insuranceDocumentUrl: "$insuranceUrl",
                    carPhotoFront: "$photoFront",
                    carPhotoBack: "$photoBack",
                    carPhotoLeft: "$photoLeft",
                    carPhotoDriverSeat: "$photoDriverSeat"
                )
            }
        """.trimIndent()

        return try {
            val response = api.registerVehicle(GraphQLRequest(query))
            if (response.errors != null) {
                Result.failure(Exception(response.errors.first().message))
            } else {
                Result.success(response.data?.get("registerVehicle") ?: false)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadImage(imageUri: String): Result<String> {
        return try {
            // In a real app, you'd convert URI to File path or use ContentResolver
            val file = File(imageUri)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
            val response = api.uploadImage(body)
            Result.success(response["url"] ?: throw Exception("Upload failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
