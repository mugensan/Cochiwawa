package com.kevinroditi.cochiwawa.data.remote.graphql

data class GraphQLResponse<T>(
    val data: T?,
    val errors: List<GraphQLError>? = null
)

data class GraphQLError(
    val message: String
)
