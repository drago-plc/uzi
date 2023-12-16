package com.lomolo.uzi

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel: ViewModel() {
    private val _deviceDetails: MutableStateFlow<DeviceDetails> = MutableStateFlow(DeviceDetails())
    val deviceDetailsUiState = _deviceDetails.asStateFlow()

    fun setDeviceLocation(gps: LatLng) {
        _deviceDetails.update {
            it.copy(deviceLocation = gps)
        }
    }
}

data class DeviceDetails(
    val deviceLocation: LatLng = LatLng(-1.3696, 36.6667)
)