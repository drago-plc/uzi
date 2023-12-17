package com.lomolo.uzi

import android.os.Bundle
import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.lomolo.uzi.permissions.LocationPermission
import com.lomolo.uzi.ui.theme.UziTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private lateinit var locationServices: LocationPermission
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationPriority: Int = Priority.PRIORITY_HIGH_ACCURACY
    private val mainViewModel: MainViewModel by viewModels { UziViewModelProvider.Factory }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            locationServices = LocationPermission
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            var shouldShowPermissionRationale by remember { mutableStateOf(false) }
            var shouldRedirectToUserLocationSettings by remember { mutableStateOf(false) }
            var hasLocationPermission by remember { mutableStateOf(locationServices.checkSelfLocationPermission(this)) }
            val locationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions(),
            ) { permissions ->
                when {
                    permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                        hasLocationPermission = true
                        locationPriority = Priority.PRIORITY_HIGH_ACCURACY
                    }
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                        hasLocationPermission = true
                        locationPriority = Priority.PRIORITY_BALANCED_POWER_ACCURACY
                    }
                    else -> {
                        shouldShowPermissionRationale = ActivityCompat
                            .shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                    }
                }
                if (hasLocationPermission) {
                    fusedLocationClient.getCurrentLocation(
                        locationPriority,
                        CancellationTokenSource().token
                    )
                        .addOnSuccessListener { location: Location? ->
                            if (location != null) {
                                mainViewModel.setDeviceLocation(LatLng(
                                    location.latitude,
                                    location.longitude
                                ))
                            }
                        }
                }

                shouldRedirectToUserLocationSettings = !shouldShowPermissionRationale && !hasLocationPermission
            }

            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(hasLocationPermission, lifecycleOwner) {
                val locationRequest = LocationRequest.Builder(locationPriority, TimeUnit.SECONDS.toMillis(3)).build()
                val locationCallback: LocationCallback = object: LocationCallback() {
                    override fun onLocationResult(p0: LocationResult) {
                        for (location in p0.locations) {
                            mainViewModel.setDeviceLocation(LatLng(location.latitude, location.longitude))
                        }
                    }
                }
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_START && !hasLocationPermission && !shouldShowPermissionRationale) {
                        locationPermissionLauncher.launch(locationServices.permissions)
                    } else if (event == Lifecycle.Event.ON_START && hasLocationPermission) {
                        fusedLocationClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                    } else if (hasLocationPermission && event == Lifecycle.Event.ON_STOP) {
                        fusedLocationClient.removeLocationUpdates(locationCallback)
                    } else if (hasLocationPermission && event == Lifecycle.Event.ON_PAUSE) {
                        fusedLocationClient.removeLocationUpdates(locationCallback)
                    }
                }

                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            UziTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (shouldShowPermissionRationale) {
                        AlertDialog(
                            onDismissRequest = { return@AlertDialog },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        shouldShowPermissionRationale = false
                                        locationPermissionLauncher.launch(locationServices?.permissions)
                                    }
                                ) {
                                    Text(
                                        text = "Approve",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }

                            },
                            title = {
                                Text("Location required")
                            },
                            text = {
                                Text(
                                    text = "You experience here would be better if we know your device location.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            icon = {
                                Icon(
                                    Icons.TwoTone.Info,
                                    contentDescription = "content info"
                                )
                            }
                        )
                    }
                    UziApplication()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UziAppPreview() {
    UziTheme {
        UziApp()
    }
}