package com.example.anvil.ui.pages

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType.Companion.PrimaryNotEditable
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.anvil.HomeScreen
import com.example.anvil.R
import com.example.anvil.data.LocationRuleCondition
import com.example.anvil.data.RuleType
import com.example.anvil.data.geofence.GeofenceManager
import com.example.anvil.ui.AnvilViewModel
import com.example.anvil.ui.theme.primaryLight
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigureLocationRuleScaffold(navController: NavHostController, viewModel: AnvilViewModel, context: Context, geofenceManager: GeofenceManager) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = primaryLight,
                ),
                title = {
                    Row {
                        Text(stringResource(R.string.configure_location_rule), fontSize = 30.sp)
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
        },
        floatingActionButton = {
            val rule by viewModel.locationRule.collectAsStateWithLifecycle()
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.surfaceDim,
                contentColor = primaryLight,
                onClick = {
                    viewModel.saveLocationRule(rule)
                    if (rule.ruleCondition == LocationRuleCondition.Enter.ordinal) {

                        geofenceManager.addGeofence(
                            id = rule.locationName,
                            location = Location("").apply {
                                latitude = rule.latitude
                                longitude = rule.longitude
                            },
                            radius = rule.radius,
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
                    } else if (rule.ruleCondition == LocationRuleCondition.Leave.ordinal) {

                        geofenceManager.addGeofence(
                            id = rule.locationName,
                            location = Location("").apply {
                                latitude = rule.latitude
                                longitude = rule.longitude
                            },
                            radius = rule.radius,
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
                        Log.d("Geofence", "Geofence added")


                    }

                    navController.popBackStack(route = HomeScreen, inclusive = false)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
            }
        }
    ) { innerPadding ->



        ConfigureLocationRuleCard(innerPadding, navController, viewModel, context)

    }
}


@Composable
fun ConfigureLocationRuleCard(innerPadding: PaddingValues = PaddingValues(8.dp), navController: NavHostController, viewModel: AnvilViewModel, context: Context) {
    val rule by viewModel.locationRule.collectAsStateWithLifecycle()
    var number by remember { mutableIntStateOf(0) }

    OutlinedCard(
        colors = CardDefaults.cardColors( containerColor = MaterialTheme.colorScheme.onPrimary ),
        border = BorderStroke(4.dp, MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier
            .fillMaxWidth()
            .padding(innerPadding)
    ) {
        LazyColumn {
            item {

                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.background,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp, horizontal = 10.dp)
                        ) {

                            Column {
                                BasicText(
                                    text = stringResource(R.string.location_name),
                                    autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 24.sp, stepSize = 0.25.sp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp, horizontal = 40.dp),
                                    maxLines = 1,
                                    minLines = 1,
                                    style = TextStyle(lineBreak = LineBreak.Heading, textAlign = TextAlign.Center, color = LocalContentColor.current)
                                    
                                )
                                TextField(
                                    value = rule.locationName,
                                    onValueChange = { viewModel.updateLocationRuleLocationName(it) },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxSize()
                                )
                            }

                        }

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        Column {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                                modifier = Modifier
                                    .padding(vertical = 6.dp, horizontal = 10.dp)
                            ) {
                                Column {
                                    BasicText(
                                        text = stringResource(R.string.when_should_the_rule_take_effect),
                                        autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 24.sp, stepSize = 0.25.sp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp, horizontal = 40.dp),
                                        maxLines = 1,
                                        minLines = 1,
                                        style = TextStyle(lineBreak = LineBreak.Heading, textAlign = TextAlign.Center, color = LocalContentColor.current)
                                        
                                    )
                                    ExposedDropdownMenuLocationRule(
                                        stringArrayResource(R.array.location_conditions),
                                        viewModel,
                                        context,
                                        rule.ruleCondition
                                    )
                                }
                            }
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                                modifier = Modifier
                                    .padding(vertical = 6.dp, horizontal = 10.dp)
                            ) {
                                Column {
                                    BasicText(
                                        text = stringResource(R.string.rule_type),
                                        autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 24.sp, stepSize = 0.25.sp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp, horizontal = 40.dp),
                                        maxLines = 1,
                                        minLines = 1,
                                        style = TextStyle(lineBreak = LineBreak.Heading, textAlign = TextAlign.Center, color = LocalContentColor.current)
                                        
                                    )
                                    ExposedDropdownMenuLocationRule(
                                        stringArrayResource(R.array.rule_types),
                                        viewModel,
                                        context,
                                        rule.ruleType
                                    )
                                }
                            }
                            when (rule.ruleType) {
                                RuleType.MuteUnmute.ordinal -> {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                                        modifier = Modifier
                                            .padding(vertical = 6.dp, horizontal = 10.dp)
                                    ) {
                                        Column {
                                            BasicText(
                                                text = stringResource(R.string.mute_unmute),
                                                autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 24.sp, stepSize = 0.25.sp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 10.dp, horizontal = 40.dp),
                                                maxLines = 1,
                                                minLines = 1,
                                                style = TextStyle(lineBreak = LineBreak.Heading, textAlign = TextAlign.Center, color = LocalContentColor.current)
                                                
                                            )
                                            when (rule.ruleBool) {
                                                true -> ExposedDropdownMenuLocationRule(stringArrayResource(R.array.mute_unmute), viewModel, context, 0)
                                                false -> ExposedDropdownMenuLocationRule(stringArrayResource(R.array.mute_unmute), viewModel, context, 1)
                                                else -> ExposedDropdownMenuLocationRule(stringArrayResource(R.array.mute_unmute), viewModel, context, 0)
                                            }
                                        }
                                    }
                                }
                                RuleType.Volume.ordinal -> {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp, horizontal = 10.dp)
                                    ) {
                                        Column {
                                            BasicText(
                                                text = stringResource(R.string.volume_percentage),
                                                autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 24.sp, stepSize = 0.25.sp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 10.dp, horizontal = 40.dp),
                                                maxLines = 1,
                                                minLines = 1,
                                                style = TextStyle(lineBreak = LineBreak.Heading, textAlign = TextAlign.Center, color = LocalContentColor.current)
                                                
                                            )
                                            Row {
                                                var sliderPosition by remember { mutableFloatStateOf(0f) }
                                                Slider(
                                                    value = sliderPosition,
                                                    onValueChange = {
                                                        sliderPosition = it
                                                        number = sliderPosition.roundToInt()
                                                        viewModel.updateLocationRuleValue(number)
                                                        viewModel.updateLocationRuleBool(null)
                                                    },
                                                    valueRange = 0f..100f,
                                                    modifier = Modifier.padding(horizontal = 20.dp)
                                                )
                                            }
                                            BasicText(
                                                text = number.toString(),
                                                autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 24.sp, stepSize = 0.25.sp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 10.dp, horizontal = 40.dp),
                                                maxLines = 1,
                                                minLines = 1,
                                                style = TextStyle(lineBreak = LineBreak.Heading, textAlign = TextAlign.Center, color = LocalContentColor.current)
                                                
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuLocationRule(options: Array<String>, viewModel: AnvilViewModel, context: Context, ordinal: Int) {
    var expanded by remember { mutableStateOf(false) }
    var choice by remember { mutableStateOf(options[ordinal]) }
    viewModel.checkLocationRuleInput(options[ordinal], context)
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
                        viewModel.checkLocationRuleInput(option, context)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}