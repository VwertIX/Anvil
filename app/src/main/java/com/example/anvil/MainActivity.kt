package com.example.anvil


import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType.Companion.PrimaryNotEditable
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.anvil.data.AppInfo
import com.example.anvil.data.LocationRule
import com.example.anvil.data.LocationRuleCondition
import com.example.anvil.data.geofence.GeofenceBroadcastReceiver
import com.example.anvil.data.geofence.GeofenceManager
import com.example.anvil.ui.AnvilViewModel
import com.example.anvil.ui.pages.AddRuleScaffold
import com.example.anvil.ui.pages.ConfigureLocationRuleScaffold
import com.example.anvil.ui.pages.HomepageScaffold
import com.example.anvil.ui.pages.ReadmeLocationScaffold
import com.example.anvil.ui.pages.ReadmeScaffold
import com.example.anvil.ui.pages.SelectAppScaffold
import com.example.anvil.ui.pages.SelectLocationScaffold
import com.example.anvil.ui.pages.SelectRuleScaffold
import com.example.anvil.ui.theme.AnvilTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable


@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_BACKGROUND_LOCATION, false) -> {
                // Precise location access granted.
            }
            else -> {
                // No location access granted.
            }
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    lateinit var geofencingClient: GeofencingClient




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val context: Context = this
        //requestPermissions() // request location access
        //requestBackgroundPermission() // request location access

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        geofencingClient = LocationServices.getGeofencingClient(this)


        setContent {
            AnvilTheme(darkTheme = true) {
                AnvilNav(context, locationPermissionRequest, fusedLocationClient, geofencingClient)
            }
        }
    }
}

@Serializable
object HomeScreen

@Serializable
object RuleChoiceScreen

@Serializable
object SelectAppScreen

@Serializable
object AddRuleScreen

@Serializable
object SelectLocationScreen

@Serializable
object ConfigureLocationRuleScreen

@Serializable
object ReadmeScreen

@Serializable
object ReadmeLocationScreen

private const val CUSTOM_INTENT_GEOFENCE = "GEOFENCE-TRANSITION-INTENT-ACTION"
private const val CUSTOM_REQUEST_CODE_GEOFENCE = 1001


// Use the 'viewModel()' function from the lifecycle-viewmodel-compose artifact
@Composable
fun AnvilNav(context: Context, locationPermissionRequest: ActivityResultLauncher<Array<String>>, fusedLocationClient: FusedLocationProviderClient, geofencingClient: GeofencingClient, viewModel: AnvilViewModel = viewModel()) {
    val packageList = AppInfo(viewModel, context)
    val navController = rememberNavController()
    val geofenceManager by remember { mutableStateOf(GeofenceManager(context)) }
    val geoFenceList by viewModel.allLocationRules.collectAsStateWithLifecycle()
    var geofenceTransitionEventInfo by remember {mutableStateOf("")}
    val scope = rememberCoroutineScope()


    for (geofence: LocationRule in geoFenceList.locationRules) {
        if (geofence.ruleCondition == LocationRuleCondition.Enter.ordinal) {

            geofenceManager.addGeofence(
                id = geofence.locationName,
                location = Location("").apply {
                    latitude = geofence.latitude
                    longitude = geofence.longitude
                },
                radius = geofence.radius,
                transitionType = GEOFENCE_TRANSITION_ENTER
            )
            if (geofenceManager.geofenceList.isNotEmpty()) {
                geofenceManager.registerGeofence()
            } else {
                Toast.makeText(
                    context,
                    "Please add at least one geofence to monitor",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            Log.d("Geofence", "Geofence added")
        } else if (geofence.ruleCondition == LocationRuleCondition.Leave.ordinal) {

            geofenceManager.addGeofence(
                id = geofence.locationName,
                location = Location("").apply {
                    latitude = geofence.latitude
                    longitude = geofence.longitude
                },
                radius = geofence.radius,
                transitionType = GEOFENCE_TRANSITION_EXIT
            )
            if (geofenceManager.geofenceList.isNotEmpty()) {
                geofenceManager.registerGeofence()
            } else {
                Toast.makeText(
                    context,
                    "Please add at least one geofence to monitor",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }




    // Register a local broadcast to receive activity transition updates
    GeofenceBroadcastReceiver(systemAction = CUSTOM_INTENT_GEOFENCE) { event ->
        geofenceTransitionEventInfo = event
    }



    NavHost(
        navController = navController,
        startDestination = HomeScreen
    ) {
        composable<HomeScreen> {
            HomepageScaffold(navController, viewModel, packageList, context, geofenceManager)

        }
        composable<RuleChoiceScreen> {
            SelectRuleScaffold(context, navController, viewModel, locationPermissionRequest, fusedLocationClient)

        }
        composable<SelectAppScreen> {
            SelectAppScaffold(navController, viewModel, packageList)

        }
        composable<SelectLocationScreen> {
            SelectLocationScaffold(context, navController, viewModel, geofenceManager, fusedLocationClient, geofencingClient)

        }
        composable<ConfigureLocationRuleScreen> {
            ConfigureLocationRuleScaffold(navController, viewModel, context, geofenceManager)

        }
        composable<AddRuleScreen> {
            AddRuleScaffold(navController, viewModel, packageList, context)
        }
        composable<ReadmeScreen> {
            ReadmeScaffold(geofenceManager)
        }
        composable<ReadmeLocationScreen> {
            ReadmeLocationScaffold()
        }
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuAppRule(options: Array<String>, viewModel: AnvilViewModel, context: Context, ordinal: Int) {
    var expanded by remember { mutableStateOf(false) }
    var choice by remember { mutableStateOf(options[ordinal]) }
    viewModel.checkAppRuleInput(options[ordinal], context)
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {

        TextField(
            // The `menuAnchor` modifier must be passed to the text field to handle
            // expanding/collapsing the menu on click. A read-only text field has
            // the anchor type `PrimaryNotEditable`.
            value = choice,
            onValueChange = { },
            readOnly = true,
            singleLine = true,
            label = { "Label" },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor(PrimaryNotEditable, true)
                .fillMaxWidth(),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        choice = option
                        viewModel.checkAppRuleInput(option, context)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
