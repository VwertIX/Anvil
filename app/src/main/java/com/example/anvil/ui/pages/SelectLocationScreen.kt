package com.example.anvil.ui.pages

import android.content.Context
import android.location.Location
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.anvil.R
import com.example.anvil.ReadmeLocationScreen
import com.example.anvil.ReadmeScreen
import com.example.anvil.RuleChoiceScreen
import com.example.anvil.data.geofence.GeofenceManager
import com.example.anvil.data.geofence.MapScreen
import com.example.anvil.ui.AnvilViewModel
import com.example.anvil.ui.theme.RedWarning
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectLocationScaffold(context: Context, navController: NavHostController, viewModel: AnvilViewModel, geofenceManager: GeofenceManager, fusedLocationClient: FusedLocationProviderClient, geofencingClient: GeofencingClient) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {


                    Row {
                        Text("Tap to select a location", fontSize = 30.sp)


                                Spacer(Modifier.weight(1f))

                                IconButton(
                                    onClick = {
                                        navController.navigate(ReadmeLocationScreen) },
                                    modifier = Modifier
                                        .size(50.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Info,
                                        contentDescription = stringResource(R.string.readme),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                    }

                }

            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .height(25.dp),
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {

            }
        }
    ) { innerPadding ->

        MapScreen(navController, viewModel, fusedLocationClient, innerPadding)

/*
        geofenceManager.addGeofence(
            "vatican_city",
            location = Location("").apply {
                latitude = 41.90238
                longitude = 12.45398
            },
        )

*/
    }
}

fun addGeofence (name: String, latitude: Double, longitude: Double, geofenceManager: GeofenceManager) {

    geofenceManager.addGeofence(
        name,
        location = Location("").apply {
            latitude
            longitude
        },
    )

}