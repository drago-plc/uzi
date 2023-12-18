package com.lomolo.uzi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.lomolo.uzi.network.UziRestApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val uziRestApiService: UziRestApiService
): ViewModel() {
    private val _deviceDetails: MutableStateFlow<DeviceDetails> = MutableStateFlow(DeviceDetails())
    val deviceDetailsUiState = _deviceDetails.asStateFlow()

    fun setDeviceLocation(gps: LatLng) {
        _deviceDetails.update {
            it.copy(gps = gps)
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = uziRestApiService.getIpinfo()
                val ipGps = response.location.split(",")
                _deviceDetails.update {
                    it.copy(
                        gps = LatLng(ipGps[0].toDouble(), ipGps[1].toDouble()),
                        country = response.country
                    )
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}

data class DeviceDetails(
    val gps: LatLng = LatLng(0.0, 0.0),
    val country: String = ""
)