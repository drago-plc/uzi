package com.lomolo.uzi

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel: ViewModel() {
    private val _deviceDetails: MutableStateFlow<DeviceDetails> = MutableStateFlow(DeviceDetails())
    val deviceDetailsUiState = _deviceDetails.asStateFlow()
    val defaultGps = LatLng(-1.3693693693693694,36.677141346659695)

    fun setDeviceLocation(gps: LatLng) {
        _deviceDetails.update {
            it.copy(gps = gps)
        }
    }
}

data class DeviceDetails(
    val gps: LatLng = LatLng(0.0, 0.0)
)