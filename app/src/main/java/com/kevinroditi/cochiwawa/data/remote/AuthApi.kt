package com.kevinroditi.cochiwawa.data.remote

import com.kevinroditi.cochiwawa.data.remote.dto.LoginData
import com.kevinroditi.cochiwawa.data.remote.dto.RegisterData
import com.kevinroditi.cochiwawa.data.remote.graphql.GraphQLRequest
import com.kevinroditi.cochiwawa.data.remote.graphql.GraphQLResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("graphql")
    suspend fun login(
        @Body request: GraphQLRequest
    ): GraphQLResponse<LoginData>

    @POST("graphql")
    suspend fun register(
        @Body request: GraphQLRequest
    ): GraphQLResponse<RegisterData>
}
