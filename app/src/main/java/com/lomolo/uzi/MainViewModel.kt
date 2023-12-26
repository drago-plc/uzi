package com.lomolo.uzi

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

class MainViewModel(
    private val uziRestApiService: UziRestApiServiceInterface
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
                        country = response.country,
                        countryFlag = response.countryFlag,
                        countryPhoneCode = countryPhoneCode[response.country]!!
                    )
                }
            } catch (e: Throwable) {
                deviceDetailsState = DeviceDetailsUiState.Error(e.localizedMessage)
                e.printStackTrace()
            }
        }
    }

    fun setHasGps(b: Boolean) {
        _deviceDetails.update {
            it.copy(hasGps = b)
        }
    }

    fun setMapLoaded(l: Boolean) {
        _deviceDetails.update {
            it.copy(mapLoaded = l)
        }
    }

    init {
       getIpinfo()
    }
}

data class DeviceDetails(
    val gps: LatLng,
    val country: String,
    val countryFlag: String,
    val countryPhoneCode: String,
    val hasGps: Boolean,
    val mapLoaded: Boolean
) {
    constructor(): this(
        LatLng(0.0, 0.0),
        "",
        "",
        "",
        false,
        false
    )
}

interface DeviceDetailsUiState {
    data object Loading: DeviceDetailsUiState
    data object Success: DeviceDetailsUiState
    data class Error(val message: String?): DeviceDetailsUiState
}