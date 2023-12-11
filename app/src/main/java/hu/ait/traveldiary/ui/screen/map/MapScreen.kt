package hu.ait.traveldiary.ui.screen.map

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
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
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    mapViewModel: MyMapViewModel = hiltViewModel(),
) {
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

    val cityPhoto = mapViewModel.getCityPics().collectAsState(
        initial = MapScreenUIState.Init
    )

    val cityLatLng = mutableMapOf<String, LatLng>()

    val cityToPhoto =
        mutableMapOf<String, String>()

    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.where_i_ve_gone)
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }

    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(bottom = 50.dp)
        ) {
            val fineLocationPermissionState = rememberPermissionState(
                Manifest.permission.ACCESS_FINE_LOCATION
            )

            LaunchedEffect(key1 = Unit) {
                fineLocationPermissionState.launchPermissionRequest()
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraState,
                properties = mapProperties,
                uiSettings = uiSettings
            ) {
                val geocoder = Geocoder(context, Locale.ENGLISH)

                val maxResult = 1

                if (cityPhoto.value is MapScreenUIState.Success) {
                    (cityPhoto.value as MapScreenUIState.Success).citiesAndPhoto.forEach {
                        if (!it.imgUrl.isEmpty()) {
                            var locationState by remember {
                                mutableStateOf(LatLng(0.0, 0.0))
                            }
                            geocoder.getFromLocationName(
                                it.cityName,
                                maxResult,
                                object : GeocodeListener {
                                    override fun onGeocode(addresses: MutableList<Address>) {
                                        locationState = LatLng(
                                            addresses.get(0).latitude,
                                            addresses.get(0).longitude
                                        )
                                    }
                                }
                            )
                            cityLatLng[it.cityName] = locationState
                            cityToPhoto[it.cityName] = it.imgUrl
                        }

                    }
                }

                cityLatLng.forEach { entry ->
                    if (entry.value != LatLng(0.0, 0.0)) {
                        MapMarker(
                            position = entry.value,
                            title = entry.key,
                            context = LocalContext.current,
                            iconResourceId = R.drawable.mapicon,
                            onClick = {
                                // Handle marker click here, e.g., show a dialog
                                showSheet = true
                            }
                        )

                        if (showSheet) {
                            BottomSheet (
                                imgUrl = cityToPhoto[entry.key]!!,
                                onDismiss = {
                                    showSheet = false
                                },
                            )

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MapMarker(
    context: Context,
    position: LatLng,
    title: String,
    @DrawableRes iconResourceId: Int,
    onClick: () -> Unit
) {
    val icon = bitmapDescriptor(
        context, iconResourceId
    )

    Marker(
        state = MarkerState(position = position),
        title = title,
        icon = icon,
        onClick = {
            onClick()
            true
        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    imgUrl: String,
    onDismiss: () -> Unit,
) {
    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {

        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = imgUrl,
                modifier = Modifier.size(300.dp),
                contentDescription = "Pin Image"
            )
        }
    }
}