package com.kevinroditi.cochiwawa.data.remote.graphql

data class GraphQLRequest(
    val query: String,
    val variables: Map<String, Any>? = null
)
