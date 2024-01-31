package com.lomolo.uzi.compose.trip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CustomCap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.lomolo.uzi.R
import com.lomolo.uzi.compose.navigation.Navigation

object TripProductsScreenDestination: Navigation {
    override val route = "trip/products"
    override val title = null
}

data class product(
    val id: Int,
    val name: String,
    val desc: String,
    val url: String
)
val products = listOf(
    product(
        1,
        "UziX",
        "Faster|Cheaper|Eco-friendly",
        "https://uzi-images.s3.eu-west-2.amazonaws.com/icons8-bike-50.png"
    ),
    product(
        2,
        "UziBoda",
        "Convenient|On-demand",
        "https://uzi-images.s3.eu-west-2.amazonaws.com/icons8-motorbike-50.png"
    )
)

@Composable
fun TripProducts(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    tripViewModel: TripViewModel
) {
    var polyline: List<LatLng> = listOf()
    when(val s = tripViewModel.makeTripRouteState) {
        is MakeTripRouteState.Success -> {
            if (s.success?.polyline != null) {
                polyline = PolyUtil.decode(s.success.polyline)
            }
        }
    }
    val uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = false))
    }

    val mapProperties by remember {
        mutableStateOf(MapProperties(mapType = MapType.TERRAIN))
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(polyline.first(), 15f)
    }
    var isMapLoaded by rememberSaveable {
        mutableStateOf(false)
    }


    Box(modifier.fillMaxSize()) {
        GoogleMap(
            properties = mapProperties,
            uiSettings = uiSettings,
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            onMapLoaded = { isMapLoaded = true }
        ) {
            if (polyline.isNotEmpty()) {
                Marker(
                    state = MarkerState(polyline[0]),
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.icons8_location_pin_96)
                )
            }
            Polyline(
                points = polyline,
                geodesic = true,
                startCap = CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.icons8_filled_circle_30)),
                endCap = CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.icons8_filled_circle_30))
            )
            if (polyline.isNotEmpty()) {
                Marker(
                    state = MarkerState(polyline[polyline.size-1]),
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.icons8_location_pin_96)
                )
            }
        }
        if (isMapLoaded) {
            Box(Modifier.padding(8.dp)) {
                IconButton(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceTint,
                            CircleShape
                        ),
                    onClick = { navigateBack() }
                ) {
                   Icon(
                       Icons.AutoMirrored.TwoTone.ArrowBack,
                       tint = MaterialTheme.colorScheme.background,
                       contentDescription = null
                   )
                }
            }
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(start = 8.dp, end = 8.dp)
            ) {
                LazyColumn {
                    items(products) {
                        ListItem(
                            headlineContent = {
                                Text(it.name)
                            },
                            supportingContent = {
                                Text(it.desc)
                            },
                            leadingContent = {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(it.url)
                                        .crossfade(true)
                                        .build(),
                                    placeholder = painterResource(id = R.drawable.loading_img),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(32.dp)
                                )
                            },
                            modifier = Modifier
                                .padding(8.dp)
                                .border(
                                    BorderStroke(
                                        2.dp,
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                ),
                            trailingContent = {
                                Text(
                                    "KES 1,000",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        )
                    }
                    item {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(8.dp),
                            shape = MaterialTheme.shapes.small,
                            onClick = { /*TODO*/ }
                        ) {
                           Text(
                               "Confirm",
                               style = MaterialTheme.typography.labelMedium
                           )
                        }
                    }
                }
            }
        }
    }
}