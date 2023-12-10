package hu.ait.traveldiary.ui.screen.map

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.location.Location
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import hu.ait.traveldiary.R
import hu.ait.traveldiary.data.CityWithPhoto
import hu.ait.traveldiary.data.Post
import hu.ait.traveldiary.ui.screen.feed.FeedScreenUIState
import hu.ait.traveldiary.ui.screen.feed.PostCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    mapViewModel: MyMapViewModel = hiltViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()

    var context = LocalContext.current

    var cameraState = rememberCameraPositionState {
        CameraPosition.fromLatLngZoom(
            LatLng(47.0, 19.0), 10f
        )
    }

    var uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = true,
                zoomGesturesEnabled = true
            )
        )
    }
    var mapProperties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                isTrafficEnabled = true
            )
        )
    }
    var isSatellite by remember {
        mutableStateOf(false)
    }

    val cityPhoto = mapViewModel.getCityPics().collectAsState(
        initial = MapScreenUIState.Init)

    val cityLatLng = mutableMapOf<String, LatLng>()

    val cityToPhoto =
        mutableMapOf<String, String>()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Switch(
                            checked = isSatellite,
                            onCheckedChange = {
                                isSatellite = it
                                mapProperties = mapProperties.copy(
                                    mapType = if (isSatellite) MapType.SATELLITE else MapType.NORMAL
                                )
                            }
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "Where I've gone",
                            style = MaterialTheme.typography.headlineLarge,
                            fontStyle = FontStyle.Normal,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(
                            onClick = {
                                // Handle menu icon click
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Toggle drawer",
                                modifier = Modifier.size(35.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                ) {
                    IconButton(
                        onClick = {
                            // Handle menu icon click
                        },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Toggle drawer",
                            modifier = Modifier.size(80.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(10f))

                    IconButton(
                        onClick = {
                            // Handle menu icon click
                        },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Toggle drawer",
                            modifier = Modifier.size(80.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(10f))

                    IconButton(
                        onClick = {
                            // Handle menu icon click
                        },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Toggle drawer",
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }
            }
        }

    ) {
        Column(modifier = Modifier.padding(it)) {
            val fineLocationPermissionState = rememberPermissionState(
                Manifest.permission.ACCESS_FINE_LOCATION
            )



            LaunchedEffect(key1 = Unit) {
                fineLocationPermissionState.launchPermissionRequest()
            }


            if (fineLocationPermissionState.status.isGranted) {
                mapViewModel.startLocationMonitoring()
                Text(
                    text = "Location: ${getLocationText(mapViewModel.locationState.value)} "
                )
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraState,
                properties = mapProperties,
                uiSettings = uiSettings
            ) {

//                LaunchedEffect(key1 = cityPhoto) {
                val geocoder = Geocoder(context, Locale.ENGLISH)

                val maxResult = 1

                Log.d("HELP", "before succes")

                if (cityPhoto.value is MapScreenUIState.Success) {
                    Log.d("HELP", "passed succes")
                    (cityPhoto.value as MapScreenUIState.Success).citiesAndPhoto.forEach {
                        var locationState by remember {
                            mutableStateOf(LatLng(0.0, 0.0))
                        }
                        geocoder.getFromLocationName(it.cityName,maxResult,object: GeocodeListener {
                            override fun onGeocode(addresses: MutableList<Address>) {
                                locationState = LatLng(addresses.get(0).latitude, addresses.get(0).longitude)
                                Log.d("HELP", locationState.toString())
                            }
                        })
                        cityLatLng[it.cityName] = locationState
                        cityToPhoto[it.cityName] = it.imgUrl
                    }

                }

//                }
                cityLatLng.forEach { entry ->
                    Log.d("HELP", entry.key)
                    if (entry.value != LatLng(0.0, 0.0)) {
//                            Marker(
//                                state = MarkerState(
//                                    position = entry.value
//                                ),
//                                title = entry.key,
//                            )

                        MapMarker(
                            position = entry.value,
                            title = entry.key,
                            context = LocalContext.current,
                            iconResourceId = R.drawable.mapicon
                        )


                    }
                }



            }
        }
    }
}

fun getLocationText(location: Location?): String {
    return """
       Lat: ${location?.latitude} 
       Lng: ${location?.longitude}
    """.trimIndent()
}

@Composable
fun MapMarker(
    context: Context,
    position: LatLng,
    title: String,
    @DrawableRes iconResourceId: Int
) {
    val icon = bitmapDescriptor(
        context, iconResourceId
    )

    Marker(
        state = MarkerState(position = position),
        title = title,
        icon = icon,
    )
}

fun bitmapDescriptor(
    context: Context,
    vectorResId: Int
): BitmapDescriptor? {

    // retrieve the actual drawable
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, 150, 150)
    val bm = Bitmap.createBitmap(
        150,
        150,
        Bitmap.Config.ARGB_8888
    )

    // draw it onto the bitmap
    val canvas = android.graphics.Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}