package com.example.anvil.data.geofence

import android.annotation.SuppressLint
import android.location.Location.distanceBetween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.anvil.ConfigureLocationRuleScreen
import com.example.anvil.ui.AnvilViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@SuppressLint("UnrememberedMutableState")
@Composable
fun MapScreen(navController: NavHostController, viewModel: AnvilViewModel, fusedLocationClient: FusedLocationProviderClient, innerPadding: PaddingValues) {
    var coord: LatLng by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    var coord2: LatLng by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    var distance: Float by remember { mutableFloatStateOf(0f) }
    LocalContext.current
    //val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }


    val userLocation = viewModel.userLocation.collectAsStateWithLifecycle()

    val cameraPositionState = rememberCameraPositionState {
        userLocation.value?.let { position = CameraPosition.fromLatLngZoom(it, 6f) }
    }
    // Display the Google Map
    GoogleMap(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        cameraPositionState = cameraPositionState,
        onMapLongClick = {
            if (coord != LatLng(0.0, 0.0)) {
                coord = LatLng(0.0, 0.0)

            }
            if (coord2 != LatLng(0.0, 0.0)) {
                coord2 = LatLng(0.0, 0.0)

            }

        },
        onMapClick = { onMapClick ->
            if (coord == LatLng(0.0, 0.0)) {
                coord = LatLng(onMapClick.latitude, onMapClick.longitude)
            } else if (coord2 == LatLng(0.0, 0.0)) {
                coord2 = LatLng(onMapClick.latitude, onMapClick.longitude)
            } else {
                val results = FloatArray(1)
                distanceBetween(coord.latitude, coord.longitude, onMapClick.latitude, onMapClick.longitude, results)
                if (distance >= results[0]) {
                    viewModel.updateLocationRuleLatLongRad(coord.latitude, coord.longitude, results[0])

                    navController.navigate(ConfigureLocationRuleScreen)

                }



            }

        }
    ) {
        if (coord != LatLng(0.0, 0.0)) {

            MarkerComposable(
                state = MarkerState(position = coord),
                title = "Location marker",
                anchor = Offset(0.5f, 0.5f),
                content = {
                    Icon(
                        Icons.Filled.AddCircle,
                        contentDescription = "Location marker",
                        modifier = Modifier.size(30.dp),
                        tint = Color.Black
                    )


                },
                onClick = { true },
                flat = true,
            )





            if (coord2 != LatLng(0.0, 0.0)) {
                val results = FloatArray(1)
                distanceBetween(coord.latitude, coord.longitude, coord2.latitude, coord2.longitude, results)
                distance = results[0]


                Circle(
                    center = coord,
                    radius = distance.toDouble(),
                    fillColor = Color(0, 129, 255, 78)
                ) {

                }

            }
        }

    }
}