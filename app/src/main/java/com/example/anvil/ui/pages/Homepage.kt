package com.example.anvil.ui.pages

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType.Companion.PrimaryNotEditable
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.anvil.R
import com.example.anvil.data.AppInfo
import com.example.anvil.data.AppRule
import com.example.anvil.data.RuleCondition
import com.example.anvil.data.RuleType
import com.example.anvil.ui.AnvilViewModel
import com.example.anvil.ui.theme.RedWarning
import kotlin.math.roundToInt
import android.media.AudioManager
import android.util.Log

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.sp
import com.example.anvil.ReadmeScreen
import com.example.anvil.RuleChoiceScreen
import com.example.anvil.data.LocationRule
import com.example.anvil.data.geofence.GeofenceManager
import com.example.anvil.ui.theme.titleMaxDimen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomepageScaffold(
    navController: NavHostController = rememberNavController(),
    viewModel: AnvilViewModel,
    packageList: AppInfo,
    context: Context,
    geofenceManager: GeofenceManager
) {
    val appRuleList by viewModel.getAllAppRules.collectAsStateWithLifecycle()
    val locationRuleList by viewModel.allLocationRules.collectAsStateWithLifecycle()

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
                        Text(stringResource(R.string.app_name), fontSize = 30.sp)

                        var check by remember { mutableStateOf(false) }
                        var confirm by remember { mutableStateOf(false) }
                        when (check) {
                            false -> {

                                Spacer(Modifier.weight(1f))

                                IconButton(
                                    onClick = { navController.navigate(ReadmeScreen) },
                                    modifier = Modifier
                                        .size(50.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Info,
                                        contentDescription = stringResource(R.string.readme),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }


                                IconButton(
                                    onClick = { check = true },
                                    modifier = Modifier
                                        .size(50.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = stringResource(R.string.delete_button_description),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }

                            }

                            true -> {
                                Spacer(Modifier.weight(1f))
                                IconButton(
                                    onClick = { confirm = true },
                                    modifier = Modifier
                                        .size(50.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = stringResource(R.string.delete_button_description),
                                        modifier = Modifier.size(30.dp),
                                        tint = RedWarning
                                    )
                                }

                                when (confirm) {
                                    false -> {}
                                    true -> {
                                        val scope = rememberCoroutineScope()
                                        AlertDialogExample(
                                            onDismissRequest = {
                                                check = false
                                                confirm = false
                                            },
                                            onConfirmation = {
                                                scope.launch(Dispatchers.IO) {
                                                    geofenceManager.deregisterGeofence()
                                                }
                                                viewModel.clearRules()
                                                check = false
                                                confirm = false
                                                println("Rule Deleted") // Add logic here to handle confirmation.
                                            },
                                            dialogTitle = "Delete all rules",
                                            dialogText = "Are you sure you want to delete all rules.",
                                            icon = Icons.Filled.Delete
                                        )
                                    }
                                }
                            }
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
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.surfaceDim,
                onClick = {
                    navController.navigate(RuleChoiceScreen)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
            }
        }
    ) { innerPadding ->

        LazyColumn(contentPadding = innerPadding) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier
                        .padding(vertical = 6.dp, horizontal = 10.dp)
                ) {
                    BasicText(
                        text = "Location Rules",
                        autoSize = TextAutoSize.StepBased(
                            minFontSize = 10.sp,
                            maxFontSize = titleMaxDimen,
                            stepSize = 0.25.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 25.dp),
                        maxLines = 2,
                        minLines = 1,
                        style = TextStyle(
                            lineBreak = LineBreak.Heading,
                            textAlign = TextAlign.Center,
                            color = LocalContentColor.current
                        )
                    )
                }

            }


            items(locationRuleList.locationRules) { rule ->

                LocationSelectionCard(rule, navController, viewModel, context, geofenceManager)
            }
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier
                        .padding(vertical = 6.dp, horizontal = 10.dp)
                ) {

                    BasicText(
                        text = "App Rules",
                        autoSize = TextAutoSize.StepBased(
                            minFontSize = 10.sp,
                            maxFontSize = titleMaxDimen,
                            stepSize = 0.25.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 25.dp),
                        maxLines = 2,
                        minLines = 1,
                        style = TextStyle(
                            lineBreak = LineBreak.Heading,
                            textAlign = TextAlign.Center,
                            color = LocalContentColor.current)
                        )

                }
            }
            items(appRuleList.appRules) { rule ->
                AppSelectionCard(rule, packageList, navController, viewModel, context)
            }

        }


    }
}



@Composable
fun LocationSelectionCard(
    locationRule: LocationRule,
    navController: NavHostController,
    viewModel: AnvilViewModel,
    context: Context,
    geofenceManager: GeofenceManager
) {

    var showDetails by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedCard(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
            border = BorderStroke(4.dp, MaterialTheme.colorScheme.inversePrimary),
            modifier = Modifier
                .padding(vertical = 6.dp, horizontal = 6.dp)
                .fillMaxWidth(),
            onClick = {
                showDetails = !showDetails
                // navController.navigate(AddRuleScreen)
            }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    /*
                Box(
                    modifier = Modifier
                        .requiredSize(130.dp)
                        .padding(vertical = 10.dp, horizontal = 10.dp)
                        .drawBehind {
                            drawIntoCanvas { canvas ->
                                launcherIcon.let {
                                    it.setBounds(
                                        0,
                                        0,
                                        size.width.roundToInt(),
                                        size.height.roundToInt()
                                    )
                                    it.draw(canvas.nativeCanvas)
                                }
                            }
                        }
                )
                */
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.background,
                            ),
                            modifier = Modifier
                                .padding(vertical = 6.dp, horizontal = 10.dp)
                        ) {
                            BasicText(
                                text = locationRule.locationName,
                                autoSize = TextAutoSize.StepBased(
                                    minFontSize = 10.sp,
                                    maxFontSize = titleMaxDimen,
                                    stepSize = 0.25.sp
                                ),
                                modifier = Modifier
                                    .padding(vertical = 10.dp, horizontal = 25.dp),
                                maxLines = 1,
                                minLines = 1,
                                style = TextStyle(
                                    lineBreak = LineBreak.Heading,
                                    textAlign = TextAlign.Center,
                                    color = LocalContentColor.current
                                )
                            )
                        }
                    }

                }
                when (showDetails) {
                    false -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                            modifier = Modifier
                                .padding(vertical = 6.dp, horizontal = 10.dp)
                                .padding(bottom = 3.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.Center) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                                    modifier = Modifier
                                        .padding(vertical = 6.dp, horizontal = 10.dp)
                                ) {
                                    val ruleConditions =
                                        stringArrayResource(R.array.location_conditions)

                                    BasicText(
                                        text = ruleConditions[locationRule.ruleCondition],
                                        autoSize = TextAutoSize.StepBased(
                                            minFontSize = 10.sp,
                                            maxFontSize = 20.sp,
                                            stepSize = 0.25.sp
                                        ),
                                        modifier = Modifier
                                            .padding(vertical = 10.dp, horizontal = 25.dp),
                                        maxLines = 1,
                                        minLines = 1,
                                        style = TextStyle(
                                            lineBreak = LineBreak.Heading,
                                            textAlign = TextAlign.Center,
                                            color = LocalContentColor.current
                                        )
                                    )
                                }

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                                    modifier = Modifier
                                        .padding(vertical = 6.dp, horizontal = 10.dp)
                                ) {

                                    when (locationRule.ruleBool) {
                                        true -> {

                                            BasicText(
                                                text = stringResource(R.string.mute),
                                                autoSize = TextAutoSize.StepBased(
                                                    minFontSize = 10.sp,
                                                    maxFontSize = 20.sp,
                                                    stepSize = 0.25.sp
                                                ),
                                                modifier = Modifier
                                                    .padding(vertical = 10.dp, horizontal = 25.dp),
                                                maxLines = 1,
                                                minLines = 1,
                                                style = TextStyle(
                                                    lineBreak = LineBreak.Heading,
                                                    textAlign = TextAlign.Center,
                                                    color = LocalContentColor.current
                                                )
                                            )
                                        }

                                        false -> {

                                            BasicText(
                                                text = stringResource(R.string.unmute),
                                                autoSize = TextAutoSize.StepBased(
                                                    minFontSize = 10.sp,
                                                    maxFontSize = 20.sp,
                                                    stepSize = 0.25.sp
                                                ),
                                                modifier = Modifier
                                                    .padding(vertical = 10.dp, horizontal = 25.dp),
                                                softWrap = false,
                                                maxLines = 1,
                                                minLines = 1,
                                                style = TextStyle(
                                                    lineBreak = LineBreak.Heading,
                                                    textAlign = TextAlign.Center,
                                                    color = LocalContentColor.current)
                                            )
                                        }

                                        else -> {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                when (locationRule.ruleType) {
                                                    RuleType.Volume.ordinal -> {
                                                        BasicText(
                                                            text = stringResource(R.string.volume) + ": " + locationRule.ruleValue.toString(),
                                                            autoSize = TextAutoSize.StepBased(
                                                                minFontSize = 10.sp,
                                                                maxFontSize = 20.sp,
                                                                stepSize = 0.25.sp
                                                            ),
                                                            modifier = Modifier
                                                                .padding(
                                                                    vertical = 10.dp,
                                                                    horizontal = 25.dp
                                                                ),
                                                            softWrap = false,
                                                            maxLines = 1,
                                                            minLines = 1,
                                                            style = TextStyle(
                                                                lineBreak = LineBreak.Heading,
                                                                textAlign = TextAlign.Center,
                                                                color = LocalContentColor.current)
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

                    true -> {

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp, horizontal = 10.dp)
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.rule_condition),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    textAlign = TextAlign.Center,
                                )
                                ExposedDropdownMenuLocationRule(
                                    stringArrayResource(R.array.location_conditions),
                                    viewModel,
                                    context,
                                    locationRule.ruleCondition,
                                    locationRule
                                )
                            }
                        }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp, horizontal = 10.dp)
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.rule_type),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    textAlign = TextAlign.Center,
                                )
                                ExposedDropdownMenuLocationRule(
                                    stringArrayResource(R.array.rule_types),
                                    viewModel,
                                    context,
                                    locationRule.ruleType,
                                    locationRule
                                )

                            }
                        }
                        when (locationRule.ruleType) {
                            RuleType.MuteUnmute.ordinal -> {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp, horizontal = 10.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = stringResource(R.string.mute_unmute),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            textAlign = TextAlign.Center,
                                        )
                                        when (locationRule.ruleBool) {
                                            true -> ExposedDropdownMenuLocationRule(
                                                stringArrayResource(R.array.mute_unmute),
                                                viewModel,
                                                context,
                                                0,
                                                locationRule
                                            )

                                            false -> ExposedDropdownMenuLocationRule(
                                                stringArrayResource(R.array.mute_unmute),
                                                viewModel,
                                                context,
                                                1,
                                                locationRule
                                            )

                                            else -> viewModel.updateLocationRule(
                                                locationRule.copy(
                                                    ruleValue = null,
                                                    ruleBool = true
                                                )
                                            )
                                        }
                                    }
                                }


                            }

                            RuleType.Volume.ordinal -> {
                                var number by remember { mutableIntStateOf(0) }
                                if (locationRule.ruleValue != null) {
                                    number = locationRule.ruleValue
                                }
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp, horizontal = 10.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = stringResource(R.string.volume_percentage),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            textAlign = TextAlign.Center,
                                        )
                                        Row {
                                            var sliderPosition by remember {
                                                mutableFloatStateOf(
                                                    number.toFloat()
                                                )
                                            }
                                            val audioManager =
                                                context.getSystemService(AudioManager::class.java)
                                            //val audioManager = getSystemService(context, AudioManager) as AudioManager

                                            val minVolume by remember {
                                                mutableIntStateOf(
                                                    audioManager.getStreamMinVolume(
                                                        AudioManager.STREAM_MUSIC
                                                    )
                                                )
                                            }
                                            val maxVolume by remember {
                                                mutableIntStateOf(
                                                    audioManager.getStreamMaxVolume(
                                                        AudioManager.STREAM_MUSIC
                                                    )
                                                )
                                            }

                                            Slider(
                                                value = sliderPosition,
                                                onValueChange = {
                                                    sliderPosition = it
                                                    number = sliderPosition.roundToInt()
                                                    viewModel.updateLocationRule(
                                                        locationRule.copy(
                                                            ruleValue = number,
                                                            ruleBool = null
                                                        )
                                                    )
                                                },
                                                valueRange = minVolume.toFloat()..maxVolume.toFloat(),
                                                modifier = Modifier.padding(horizontal = 20.dp)
                                            )
                                        }
                                        Text(
                                            text = number.toString(),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.size(20.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                            modifier = Modifier
                                .padding(bottom = 10.dp)
                        ) {
                            var check by remember { mutableStateOf(false) }
                            var confirm by remember { mutableStateOf(false) }
                            when (check) {
                                false -> {


                                    IconButton(
                                        onClick = { check = true },
                                        modifier = Modifier
                                            .size(80.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.Delete,
                                            contentDescription = stringResource(R.string.delete_button_description),
                                            modifier = Modifier.size(50.dp)
                                        )
                                    }
                                }

                                true -> {

                                    IconButton(
                                        onClick = { confirm = true },
                                        modifier = Modifier
                                            .size(80.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.Delete,
                                            contentDescription = stringResource(R.string.delete_button_description),
                                            modifier = Modifier.size(50.dp),
                                            tint = RedWarning
                                        )
                                    }
                                    when (confirm) {
                                        false -> {}
                                        true -> {
                                            val rule by viewModel.locationRule.collectAsStateWithLifecycle()
                                            val scope = rememberCoroutineScope()
                                            AlertDialogExample(
                                                onDismissRequest = {
                                                    check = false
                                                    confirm = false
                                                },
                                                onConfirmation = {

                                                    scope.launch(Dispatchers.IO) {
                                                        geofenceManager.deregisterGeofence()
                                                    }
                                                    geofenceManager.removeGeofence(rule.locationName)
                                                    //geofenceManager.registerGeofence()
                                                    viewModel.deleteLocationRule(locationRule)
                                                    check = false
                                                    confirm = false

                                                    Log.d("RunningAppsService", "Rule Deleted")
                                                },
                                                dialogTitle = "Delete Rule",
                                                dialogText = "Are you sure you want to delete this rule.",
                                                icon = Icons.Filled.Delete
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



@Composable
fun AppSelectionCard(
    appRule: AppRule,
    packageList: AppInfo,
    navController: NavHostController,
    viewModel: AnvilViewModel,
    context: Context
) {
    val launcherIcon: Drawable = packageList.getPackageIconByName(appRule.packageName)
    var showDetails by remember { mutableStateOf(false) }

    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        border = BorderStroke(4.dp, MaterialTheme.colorScheme.inversePrimary),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 6.dp),
        onClick = {
            showDetails = !showDetails
            // navController.navigate(AddRuleScreen)
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Box(
                    modifier = Modifier
                        .requiredSize(130.dp)
                        .padding(vertical = 10.dp, horizontal = 10.dp)
                        .drawBehind {
                            drawIntoCanvas { canvas ->
                                launcherIcon.let {
                                    it.setBounds(
                                        0,
                                        0,
                                        size.width.roundToInt(),
                                        size.height.roundToInt()
                                    )
                                    it.draw(canvas.nativeCanvas)
                                }
                            }
                        }
                )
                if (appRule.appName != appRule.packageName) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                            modifier = Modifier
                                .padding(vertical = 6.dp, horizontal = 10.dp)
                        ) {

                            BasicText(text = appRule.appName, autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = titleMaxDimen, stepSize = 0.25.sp), modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 25.dp), maxLines = 2, minLines = 1, style = TextStyle(
                                lineBreak = LineBreak.Heading,
                                textAlign = TextAlign.Center,
                                color = LocalContentColor.current)
                            )
                        }
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.background,
                            ),
                            modifier = Modifier
                                .padding(vertical = 6.dp, horizontal = 10.dp)
                        ) {
                            BasicText(
                                text = appRule.packageName,
                                autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = titleMaxDimen, stepSize = 0.25.sp),
                                modifier = Modifier
                                    .padding(vertical = 6.dp, horizontal = 10.dp),
                                softWrap = false,
                                maxLines = 1,
                                minLines = 1,
                                style = TextStyle(lineBreak = LineBreak.Heading, textAlign = TextAlign.Center, color = LocalContentColor.current)
                            )
                        }
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.background,
                            ),
                            modifier = Modifier
                                .padding(vertical = 6.dp, horizontal = 10.dp)
                        ) {
                            BasicText(
                                text = appRule.appName,
                                autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = titleMaxDimen, stepSize = 0.25.sp),
                                modifier = Modifier
                                    .padding(vertical = 10.dp, horizontal = 25.dp),
                                maxLines = 1,
                                minLines = 1,
                                style = TextStyle(lineBreak = LineBreak.Heading, textAlign = TextAlign.Center, color = LocalContentColor.current)
                            )
                        }
                    }
                }
            }
            when (showDetails) {
                false -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        modifier = Modifier
                            .padding(vertical = 6.dp, horizontal = 10.dp)
                            .padding(bottom = 3.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.Center) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                                modifier = Modifier
                                    .padding(vertical = 6.dp, horizontal = 10.dp)
                            ) {
                                val ruleConditions = stringArrayResource(R.array.rule_conditions)

                                BasicText(
                                    text = ruleConditions[appRule.ruleCondition],
                                    autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 20.sp, stepSize = 0.25.sp),
                                    modifier = Modifier
                                        .padding(vertical = 10.dp, horizontal = 25.dp),
                                    maxLines = 1,
                                    minLines = 1,
                                    style = TextStyle(lineBreak = LineBreak.Heading, textAlign = TextAlign.Center, color = LocalContentColor.current)
                                )
                            }

                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                                modifier = Modifier
                                    .padding(vertical = 6.dp, horizontal = 10.dp)
                            ) {

                                when (appRule.ruleBool) {
                                    true -> {

                                        BasicText(
                                            text = stringResource(R.string.mute),
                                            autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 20.sp, stepSize = 0.25.sp),
                                            modifier = Modifier
                                                .padding(vertical = 10.dp, horizontal = 25.dp),
                                            maxLines = 1,
                                            minLines = 1,
                                            style = TextStyle(lineBreak = LineBreak.Heading, textAlign = TextAlign.Center, color = LocalContentColor.current)
                                        )
                                    }

                                    false -> {

                                        BasicText(
                                            text = stringResource(R.string.unmute),
                                            autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 20.sp, stepSize = 0.25.sp),
                                            modifier = Modifier
                                                .padding(vertical = 10.dp, horizontal = 25.dp),
                                            softWrap = false,
                                            maxLines = 1,
                                            minLines = 1,
                                            style = TextStyle(lineBreak = LineBreak.Heading, textAlign = TextAlign.Center, color = LocalContentColor.current)
                                        )
                                    }

                                    else -> {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            when (appRule.ruleType) {
                                                RuleType.Volume.ordinal -> {
                                                    BasicText(
                                                        text = stringResource(R.string.volume) + ": " + appRule.ruleValue.toString(),
                                                        autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 20.sp, stepSize = 0.25.sp),
                                                        modifier = Modifier
                                                            .padding(vertical = 10.dp, horizontal = 25.dp),
                                                        softWrap = false,
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

                true -> {

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 10.dp)
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.rule_condition),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                textAlign = TextAlign.Center,
                            )
                            ExposedDropdownMenuAppRule(
                                stringArrayResource(R.array.rule_conditions),
                                viewModel,
                                context,
                                appRule.ruleCondition,
                                appRule
                            )
                        }
                    }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 10.dp)
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.rule_type),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                textAlign = TextAlign.Center,
                            )
                            ExposedDropdownMenuAppRule(
                                stringArrayResource(R.array.rule_types),
                                viewModel,
                                context,
                                appRule.ruleType,
                                appRule
                            )

                        }
                    }
                    when (appRule.ruleType) {
                        RuleType.MuteUnmute.ordinal -> {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp, horizontal = 10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = stringResource(R.string.mute_unmute),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        textAlign = TextAlign.Center,
                                    )
                                    when (appRule.ruleBool) {
                                        true -> ExposedDropdownMenuAppRule(
                                            stringArrayResource(R.array.mute_unmute),
                                            viewModel,
                                            context,
                                            0,
                                            appRule
                                        )

                                        false -> ExposedDropdownMenuAppRule(
                                            stringArrayResource(R.array.mute_unmute),
                                            viewModel,
                                            context,
                                            1,
                                            appRule
                                        )

                                        else -> viewModel.updateAppRule(
                                            appRule.copy(
                                                ruleValue = null,
                                                ruleBool = true
                                            )
                                        )
                                    }
                                }
                            }


                        }

                        RuleType.Volume.ordinal -> {
                            var number by remember { mutableIntStateOf(0) }
                            if (appRule.ruleValue != null) {
                                number = appRule.ruleValue
                            }
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp, horizontal = 10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = stringResource(R.string.volume_percentage),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        textAlign = TextAlign.Center,
                                    )
                                    Row {
                                        var sliderPosition by remember { mutableFloatStateOf(number.toFloat()) }
                                        val audioManager =
                                            context.getSystemService(AudioManager::class.java)
                                        //val audioManager = getSystemService(context, AudioManager) as AudioManager

                                        val minVolume by remember {
                                            mutableIntStateOf(
                                                audioManager.getStreamMinVolume(
                                                    AudioManager.STREAM_MUSIC
                                                )
                                            )
                                        }
                                        val maxVolume by remember {
                                            mutableIntStateOf(
                                                audioManager.getStreamMaxVolume(
                                                    AudioManager.STREAM_MUSIC
                                                )
                                            )
                                        }

                                        Slider(
                                            value = sliderPosition,
                                            onValueChange = {
                                                sliderPosition = it
                                                number = sliderPosition.roundToInt()
                                                viewModel.updateAppRule(
                                                    appRule.copy(
                                                        ruleValue = number,
                                                        ruleBool = null
                                                    )
                                                )
                                            },
                                            valueRange = minVolume.toFloat()..maxVolume.toFloat(),
                                            modifier = Modifier.padding(horizontal = 20.dp)
                                        )
                                    }
                                    Text(
                                        text = number.toString(),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(20.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                    ) {
                        var check by remember { mutableStateOf(false) }
                        var confirm by remember { mutableStateOf(false) }
                        when (check) {
                            false -> {


                                IconButton(
                                    onClick = { check = true },
                                    modifier = Modifier
                                        .size(80.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = stringResource(R.string.delete_button_description),
                                        modifier = Modifier.size(50.dp)
                                    )
                                }
                            }

                            true -> {

                                IconButton(
                                    onClick = { confirm = true },
                                    modifier = Modifier
                                        .size(80.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = stringResource(R.string.delete_button_description),
                                        modifier = Modifier.size(50.dp),
                                        tint = RedWarning
                                    )
                                }
                                when (confirm) {
                                    false -> {}
                                    true -> {
                                        AlertDialogExample(
                                            onDismissRequest = {
                                                check = false
                                                confirm = false
                                            },
                                            onConfirmation = {
                                                viewModel.deleteAppRule(appRule)
                                                check = false
                                                confirm = false
                                                Log.d("RunningAppsService", "Rule Deleted")
                                            },
                                            dialogTitle = "Delete Rule",
                                            dialogText = "Are you sure you want to delete this rule.",
                                            icon = Icons.Filled.Delete
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



@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuLocationRule(
    options: Array<String>,
    viewModel: AnvilViewModel,
    context: Context,
    ordinal: Int,
    locationRule: LocationRule
) {
    var expanded by remember { mutableStateOf(false) }
    var choice by remember { mutableStateOf(options[ordinal]) }
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
                        when (choice) {
                            context.getString(R.string.mute_unmute) -> viewModel.updateLocationRule(
                                locationRule.copy(
                                    ruleType = RuleType.MuteUnmute.ordinal,
                                    ruleValue = null,
                                    ruleBool = true
                                )
                            )

                            context.getString(R.string.volume) -> viewModel.updateLocationRule(
                                locationRule.copy(
                                    ruleType = RuleType.Volume.ordinal,
                                    ruleValue = 0,
                                    ruleBool = null
                                )
                            )

                            context.getString(R.string.open) -> viewModel.updateLocationRule(
                                locationRule.copy(
                                    ruleCondition = RuleCondition.Open.ordinal
                                )
                            )

                            context.getString(R.string.close) -> viewModel.updateLocationRule(
                                locationRule.copy(
                                    ruleCondition = RuleCondition.Close.ordinal
                                )
                            )

                            context.getString(R.string.mute) -> viewModel.updateLocationRule(
                                locationRule.copy(
                                    ruleBool = true,
                                    ruleValue = null
                                )
                            )

                            context.getString(R.string.unmute) -> viewModel.updateLocationRule(
                                locationRule.copy(
                                    ruleBool = false,
                                    ruleValue = null
                                )
                            )
                        }
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuAppRule(
    options: Array<String>,
    viewModel: AnvilViewModel,
    context: Context,
    ordinal: Int,
    appRule: AppRule
) {
    var expanded by remember { mutableStateOf(false) }
    var choice by remember { mutableStateOf(options[ordinal]) }
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
                        when (choice) {
                            context.getString(R.string.mute_unmute) -> viewModel.updateAppRule(
                                appRule.copy(
                                    ruleType = RuleType.MuteUnmute.ordinal,
                                    ruleValue = null,
                                    ruleBool = true
                                )
                            )

                            context.getString(R.string.volume) -> viewModel.updateAppRule(
                                appRule.copy(
                                    ruleType = RuleType.Volume.ordinal,
                                    ruleValue = 0,
                                    ruleBool = null
                                )
                            )

                            context.getString(R.string.open) -> viewModel.updateAppRule(
                                appRule.copy(
                                    ruleCondition = RuleCondition.Open.ordinal
                                )
                            )

                            context.getString(R.string.close) -> viewModel.updateAppRule(
                                appRule.copy(
                                    ruleCondition = RuleCondition.Close.ordinal
                                )
                            )

                            context.getString(R.string.mute) -> viewModel.updateAppRule(
                                appRule.copy(
                                    ruleBool = true,
                                    ruleValue = null
                                )
                            )

                            context.getString(R.string.unmute) -> viewModel.updateAppRule(
                                appRule.copy(
                                    ruleBool = false,
                                    ruleValue = null
                                )
                            )
                        }
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

