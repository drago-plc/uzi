package com.lomolo.uzi

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.lomolo.uzi.network.UziRestApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val uziRestApiService: UziRestApiService
): ViewModel() {
    private val _deviceDetails: MutableStateFlow<DeviceDetails> = MutableStateFlow(DeviceDetails())
    val deviceDetailsUiState = _deviceDetails.asStateFlow()

    var deviceDetailsState: DeviceDetailsUiState by mutableStateOf(DeviceDetailsUiState.Loading)
        private set

    fun setDeviceLocation(gps: LatLng) {
        _deviceDetails.update {
            it.copy(gps = gps)
        }
    }

    fun getIpinfo() {
        viewModelScope.launch {
            deviceDetailsState = DeviceDetailsUiState.Loading
            try {
                val response = uziRestApiService.getIpinfo()
                val ipGps = response.location.split(",")
                _deviceDetails.update {
                    deviceDetailsState = DeviceDetailsUiState.Success
                    it.copy(
                        gps = LatLng(ipGps[0].toDouble(), ipGps[1].toDouble()),
                        country = response.country
                    )
                }
            } catch (e: Throwable) {
                deviceDetailsState = DeviceDetailsUiState.Error(e.localizedMessage)
                e.printStackTrace()
            }
        }
    }

    init {
       getIpinfo()
    }
}

data class DeviceDetails(
    val gps: LatLng = LatLng(0.0, 0.0),
    val country: String = ""
)

interface DeviceDetailsUiState {
    data object Loading: DeviceDetailsUiState
    data object Success: DeviceDetailsUiState
    data class Error(val message: String?): DeviceDetailsUiState
}