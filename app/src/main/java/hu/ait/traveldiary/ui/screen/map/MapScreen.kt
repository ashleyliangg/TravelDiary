package hu.ait.traveldiary.ui.screen.map

import android.Manifest
import android.location.Location
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    mapViewModel: MyMapViewModel = hiltViewModel(),
    cityName: String
) {
    val coroutineScope = rememberCoroutineScope()

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
                            text = "TITLE",
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
            ){
                Row (
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    ){
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
//            val fineLocationPermissionState = rememberPermissionState(
//                Manifest.permission.ACCESS_FINE_LOCATION
//            )
//        if (fineLocationPermissionState.status.isGranted) {
//            Column {
//
//                Button(onClick = {
//                    mapViewModel.startLocationMonitoring()
//                }) {
//                    Text(text = "Start location monitoring")
//                }
//                Text(
//                    text = "Location: ${getLocationText(mapViewModel.locationState.value)}"
//                )
//            }
//
//        } else {
//            Column() {
//                val permissionText = if (fineLocationPermissionState.status.shouldShowRationale) {
//                    "Please consider giving permission"
//                } else {
//                    "Give permission for location"
//                }
//                Text(text = permissionText)
//                Button(onClick = {
//                    fineLocationPermissionState.launchPermissionRequest()
//                }) {
//                    Text(text = "Request permission")
//                }
//            }
//        }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraState,
                properties = mapProperties,
                uiSettings = uiSettings,

//                onMapClick = {
//                    mapViewModel.addMarkerPosition(it)
//
//                    //centers map
//                    /*val cameraPosition = CameraPosition.Builder()
//                        .target(it)
//                        .build()*/
////                val random = Random(System.currentTimeMillis())
//                    val cameraPosition = CameraPosition.Builder()
//                        .target(it)
////                    .zoom(1f + random.nextInt(5))
////                    .tilt(30f + random.nextInt(15))
////                    .bearing(-45f + random.nextInt(90))
//                        .build()
//
//                    coroutineScope.launch {
//                        cameraState.animate(
//                            CameraUpdateFactory.newCameraPosition(cameraPosition),
//                            3000
//                        )
//                    }
//                }
            ) {


//                for (position in mapViewModel.getMarkersList()) {
//                    Marker(
//                        state = MarkerState(position = position), //should be it.latlng of the city
//                        title = cityName //should be it.cityName
////                    icon = bitmapDescriptor(context, R.drawable.)
//                    )
//                }

            }
        }
    }
}

fun getLocationText(location: Location?): String {
    return """
       Lat: ${location?.latitude}
       Lng: ${location?.longitude}
       Alt: ${location?.altitude}
       Speed: ${location?.speed}
       Accuracy: ${location?.accuracy}
    """.trimIndent()
}