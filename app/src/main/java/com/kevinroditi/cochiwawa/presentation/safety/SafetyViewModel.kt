package com.kevinroditi.cochiwawa.presentation.safety

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.kevinroditi.cochiwawa.data.remote.AuthApi
import com.kevinroditi.cochiwawa.data.remote.graphql.GraphQLRequest
import com.kevinroditi.cochiwawa.data.local.TokenStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SafetyViewModel @Inject constructor(
    private val api: AuthApi,
    private val tokenStore: TokenStore,
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {

    @SuppressLint("MissingPermission")
    fun triggerEmergency() {
        viewModelScope.launch {
            try {
                val token = tokenStore.authToken.first() ?: return@launch
                
                // Getting current location
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            sendEmergencyAlert(location.latitude, location.longitude)
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun sendEmergencyAlert(lat: Double, lng: Double) {
        viewModelScope.launch {
            val query = """
                mutation {
                    triggerEmergencyAlert(userId: "current", lat: $lat, lng: $lng)
                }
            """.trimIndent()
            
            try {
                api.login(GraphQLRequest(query)) // Generic execute would be better
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
