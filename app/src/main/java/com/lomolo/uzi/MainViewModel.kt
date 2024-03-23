package com.lomolo.uzi

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.lomolo.uzi.common.countryPhoneCode
import com.lomolo.uzi.network.UziRestApiServiceInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class MainViewModel(
    private val uziRestApiService: UziRestApiServiceInterface
): ViewModel() {
    private val _logTag = "MainViewModel"
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
                        country = response.country,
                        countryFlag = response.countryFlag,
                        countryPhoneCode = countryPhoneCode[response.country] ?: ""
                    )
                }
            } catch (e: IOException) {
                e.message?.let {
                    deviceDetailsState = DeviceDetailsUiState.Error(it)
                    e.message?.let { Log.d(_logTag, "Something went wrong $e") }
                }
            }
        }
    }

    fun setMapLoaded(loaded: Boolean) {
        _deviceDetails.update {
            it.copy(mapLoaded = loaded)
        }
    }

    init {
       getIpinfo()
    }
}

data class DeviceDetails(
    val gps: LatLng = LatLng(0.0, 0.0),
    val country: String = "",
    val countryFlag: String = "",
    val countryPhoneCode: String = "",
    val hasGps: Boolean = false,
    val mapLoaded: Boolean = false
)


interface DeviceDetailsUiState {
    data object Loading: DeviceDetailsUiState
    data object Success: DeviceDetailsUiState
    data class Error(val message: String?): DeviceDetailsUiState
}