package hu.ait.traveldiary.ui.screen.map

import android.location.Location
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.ait.traveldiary.location.LocationManager
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.ait.traveldiary.data.CityWithPhoto
import hu.ait.traveldiary.data.Post
import hu.ait.traveldiary.data.PostWithId
import hu.ait.traveldiary.ui.screen.feed.FeedScreenUIState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyMapViewModel @Inject constructor(
    val locationManager: LocationManager
) : ViewModel() {

    // --- Maps related
    private var _markerPositionList =
        mutableStateListOf<LatLng>()


    fun getCityPics() = callbackFlow {
        val snapshotListener =
            FirebaseFirestore.getInstance().collection("posts")
                .addSnapshotListener() { snapshot, e ->
                    val response = if (snapshot != null) {
                        val cityList = snapshot.toObjects(Post::class.java)

                        val nameWithUrl = mutableListOf<CityWithPhoto>()
                        val citiesChecked = mutableSetOf<String>()

                        cityList.forEachIndexed { index, post ->
                            if ( !citiesChecked.contains(post.cityName) ) {
                                nameWithUrl.add(CityWithPhoto(post.cityName, post.imgUrl))
                                citiesChecked.add(post.cityName)
                            }
                        }
                        MapScreenUIState.Success(
                            nameWithUrl
                        )
                    } else {
                        MapScreenUIState.Error(e?.message.toString())
                    }
                    trySend(response) // emit this value through the flow
                }
        awaitClose {
            snapshotListener.remove()
        }
    }


//    fun getMarkersList(): List<LatLng> {
//        return _markerPositionList
//    }
//
//    fun addMarkerPosition(latLng: LatLng) {
//        _markerPositionList.add(latLng)
//    }

    // --- Location related
    var locationState = mutableStateOf<Location?>(null)

    fun startLocationMonitoring() {
        viewModelScope.launch {
            locationManager
                .fetchUpdates()
                .collect {
                    locationState.value = it
                }
        }
    }
}

sealed interface MapScreenUIState {
    object Init : MapScreenUIState
    data class Success(val citiesAndPhoto: List<CityWithPhoto>) : MapScreenUIState
    data class Error(val error: String?) : MapScreenUIState
}