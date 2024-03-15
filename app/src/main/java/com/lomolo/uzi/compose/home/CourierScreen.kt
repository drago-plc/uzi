package com.lomolo.uzi.compose.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.uzi.GetTripDetailsQuery
import com.lomolo.uzi.R
import com.lomolo.uzi.compose.trip.TripViewModel
import java.text.NumberFormat

@Composable
internal fun Courier(
    modifier: Modifier = Modifier,
    tripViewModel: TripViewModel,
    courier: GetTripDetailsQuery.GetTripDetails
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        tripViewModel.getTripDetails()
    }
    Column {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (courier.courier != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(courier.courier.avatar?.uri)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(id = R.drawable.loading_img),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(68.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentDescription = null
                )
            }
            if (courier.courier?.user != null) {
                Column(
                    modifier = Modifier
                        .padding(start=8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "${courier.courier.user.first_name} ${courier.courier.user.last_name}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            if (courier.courier?.product != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(courier.courier.product.icon_url)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(id = R.drawable.loading_img),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(42.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentDescription = null
                )
            }
        }
        Spacer(modifier = Modifier.size(32.dp))
        Text(
            "Trip cost KES ${NumberFormat.getNumberInstance().format(courier.cost)}",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .padding(start=4.dp)
        )
        Spacer(modifier = Modifier.size(16.dp))
        if (courier.courier?.user != null) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.small,
                onClick = {
                    Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${courier.courier.user.phone}")
                    }.also { context.startActivity(it) }
                }
            ) {
                Text(
                    "Call",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}