package com.lomolo.uzi.compose.trip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CustomCap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.lomolo.uzi.ComputeTripRouteQuery
import com.lomolo.uzi.R
import com.lomolo.uzi.compose.navigation.Navigation
import java.text.NumberFormat

object TripProductsScreenDestination: Navigation {
    override val route = "trip/products"
    override val title = null
}

@Composable
fun TripProducts(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    tripViewModel: TripViewModel,
    onConfirmTrip: (String) -> Unit
) {
    val context = LocalContext.current
    var routeComputed = false
    var polyline: List<LatLng> = listOf()
    var nearbyProducts: List<ComputeTripRouteQuery.AvailableProduct> = listOf()
    when(val s = tripViewModel.makeTripRouteState) {
        is ComputeTripRouteState.Success -> {
            if (s.success?.polyline != null) {
                polyline = PolyUtil.decode(s.success.polyline)
                nearbyProducts = s.success.availableProducts
                routeComputed = true
            }
        }
    }
    val uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = false))
    }

    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                    context,
                    R.raw.style_json
                )
            )
        )
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(polyline.first(), 17f)
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
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.icons8_location_pin_90___),
                    zIndex = 1.0f
                )
            }
            Polyline(
                width = 12f,
                points = polyline,
                geodesic = true,
                startCap = CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.icons8_filled_circle_30)),
                endCap = CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.icons8_filled_circle_30))
            )
            if (polyline.isNotEmpty()) {
                Marker(
                    state = MarkerState(polyline[polyline.size-1]),
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.icons8_location_pin_90___),
                    zIndex = 1f
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
                    onClick = {
                        navigateBack()
                        tripViewModel.resetTrip()
                    }
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
                LaunchedEffect(Unit) {
                    if (polyline.isNotEmpty()) tripViewModel.getCourierNearPickup(polyline[0])
                }

               if (nearbyProducts.isNotEmpty()) {
                   NearbyProducts(
                       products = nearbyProducts,
                       onConfirmTrip = { onConfirmTrip(ConfirmTripDetailsDestination.route) },
                       tripViewModel = tripViewModel
                   )
               } else {
                   if (routeComputed) {
                       Text(
                           stringResource(R.string.still_onboarding_your_area),
                           modifier = Modifier.padding(16.dp)
                       )
                   }
               }
            }
        }
    }
}

@Composable
private fun NearbyProducts(
    modifier: Modifier = Modifier,
    products: List<ComputeTripRouteQuery.AvailableProduct>,
    onConfirmTrip: () -> Unit,
    tripViewModel: TripViewModel
) {
    LaunchedEffect(Unit) {
        if (tripViewModel.tripProductId.isBlank()) tripViewModel.setTripProduct(products[0].id.toString())
    }

    LazyColumn(
        modifier = modifier
    ) {
        items(products) {
            ListItem(
                headlineContent = {
                    Text(it.name)
                },
                supportingContent = {
                    Text(it.description)
                },
                leadingContent = {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(it.icon_url)
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
                    .padding(top = 16.dp, bottom = 16.dp)
                    .border(
                        BorderStroke(
                            2.dp,
                            if (tripViewModel.tripProductId == it.id.toString()) MaterialTheme.colorScheme.onSurfaceVariant
                            else
                            MaterialTheme.colorScheme.background
                        )
                    )
                    .clickable { tripViewModel.setTripProduct(it.id.toString()) },
                trailingContent = {
                    Text(
                        "KES ${NumberFormat.getNumberInstance().format(it.price)}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            )
        }
        item {
            Box(Modifier.padding(top= 8.dp, bottom = 8.dp)) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = MaterialTheme.shapes.small,
                    onClick = { onConfirmTrip() }
                ) {
                    Text(
                        "Confirm",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}