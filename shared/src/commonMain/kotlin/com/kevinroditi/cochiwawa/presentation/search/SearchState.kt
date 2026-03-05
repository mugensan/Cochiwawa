package com.kevinroditi.cochiwawa.presentation.search

import com.kevinroditi.cochiwawa.domain.model.Ride

data class SearchState(
    val origin: String = "",
    val destination: String = "",
    val rides: List<Ride> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
