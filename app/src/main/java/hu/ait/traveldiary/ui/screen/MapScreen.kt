package hu.ait.traveldiary.ui.screen

import android.Manifest
import android.location.Location
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    mapViewModel: MyMapViewModel = hiltViewModel()
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

    Column {

        val fineLocationPermissionState = rememberPermissionState(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (fineLocationPermissionState.status.isGranted) {
            Column {

                Button(onClick = {
                    mapViewModel.startLocationMonitoring()
                }) {
                    Text(text = "Start location monitoring")
                }
                Text(
                    text = "Location: ${getLocationText(mapViewModel.locationState.value)}"
                )
            }

        } else {
            Column() {
                val permissionText = if (fineLocationPermissionState.status.shouldShowRationale) {
                    "Please consider giving permission"
                } else {
                    "Give permission for location"
                }
                Text(text = permissionText)
                Button(onClick = {
                    fineLocationPermissionState.launchPermissionRequest()
                }) {
                    Text(text = "Request permission")
                }
            }
        }

        var isSatellite by remember {
            mutableStateOf(false)
        }
        Switch(checked = isSatellite, onCheckedChange = {
            isSatellite = it
            mapProperties = mapProperties.copy(
                mapType = if (isSatellite) MapType.SATELLITE else MapType.NORMAL
            )
        })

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            properties = mapProperties,
            uiSettings = uiSettings,
            onMapClick = {
                mapViewModel.addMarkerPosition(it)

                //centers map
                /*val cameraPosition = CameraPosition.Builder()
                    .target(it)
                    .build()*/

//                val random = Random(System.currentTimeMillis())
                val cameraPosition = CameraPosition.Builder()
                    .target(it)
//                    .zoom(1f + random.nextInt(5))
//                    .tilt(30f + random.nextInt(15))
//                    .bearing(-45f + random.nextInt(90))
                    .build()

                coroutineScope.launch {
                    cameraState.animate(
                        CameraUpdateFactory.newCameraPosition(cameraPosition),
                        3000
                    )
                }
            }
        ) {



//            Marker(
//                state = MarkerState(LatLng(47.0, 19.0)),
//                title = "Marker demo",
//                snippet = "Hungary, population 9.7M",
//                draggable = true
//            )
//
            for (position in mapViewModel.getMarkersList()) {
                Marker(
                    state = MarkerState(position = position), //should be it.latlong of the city
                    title = "Title" //should be it.cityName
//                    icon = bitmapDescriptor(context, R.drawable.)
                )
            }
//
//            Polyline(
//                points = listOf(
//                    LatLng(47.0, 19.0),
//                    LatLng(45.0, 18.0),
//                    LatLng(49.0, 23.0),
//                ),
//                color = androidx.compose.ui.graphics.Color.Red,
//                visible = true,
//                width = 10f
//            )
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