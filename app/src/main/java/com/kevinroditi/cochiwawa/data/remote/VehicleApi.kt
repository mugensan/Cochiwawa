package com.kevinroditi.cochiwawa.data.remote

import com.kevinroditi.cochiwawa.data.remote.graphql.GraphQLRequest
import com.kevinroditi.cochiwawa.data.remote.graphql.GraphQLResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface VehicleApi {

    @POST("graphql")
    suspend fun registerVehicle(
        @Body request: GraphQLRequest
    ): GraphQLResponse<Map<String, Boolean>>

    @Multipart
    @POST("api/upload")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Map<String, String>
}
