package com.example.anvil.ui.pages

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavHostController
import com.example.anvil.ExposedDropdownMenuAppRule
import com.example.anvil.HomeScreen
import com.example.anvil.R
import com.example.anvil.data.AppInfo
import com.example.anvil.data.RuleType
import com.example.anvil.ui.AnvilViewModel
import com.example.anvil.ui.theme.primaryLight
import com.example.anvil.ui.theme.titleMaxDimen
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRuleScaffold(navController: NavHostController, viewModel: AnvilViewModel, packageList: AppInfo, context: Context) {
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
                        Text(stringResource(R.string.add_a_rule), fontSize = 30.sp)
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
            val rule by viewModel.appRule.collectAsStateWithLifecycle()
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.surfaceDim,
                contentColor = primaryLight,
                onClick = dropUnlessResumed {
                    viewModel.saveAppRule(rule)
                    navController.popBackStack(route = HomeScreen, inclusive = false)
                }
            )
            {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
            }
        }
    ) { innerPadding ->



        AddRuleCard(packageList, innerPadding, navController, viewModel, context)

    }
}



@Composable
fun AddRuleCard(packageList: AppInfo, innerPadding: PaddingValues = PaddingValues(8.dp), navController: NavHostController, viewModel: AnvilViewModel, context: Context) {
    val rule by viewModel.appRule.collectAsStateWithLifecycle()
    //val appIcon = packageList.getPackageIconByName(rule.packageName)
    val appIcon by remember { mutableStateOf(packageList.getPackageIconByName(rule.packageName)) }
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
                        Box(
                            modifier = Modifier
                                .requiredSize(130.dp)
                                .padding(vertical = 10.dp, horizontal = 10.dp)
                                .drawBehind {
                                    drawIntoCanvas { canvas ->
                                        appIcon.let {
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
                        if (rule.appName != rule.packageName) {
                            Column {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.background,
                                    ),
                                    modifier = Modifier
                                        .padding(vertical = 6.dp, horizontal = 10.dp)
                                ) {
                                    BasicText(
                                        text = rule.appName,
                                        autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = titleMaxDimen, stepSize = 0.25.sp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp, horizontal = 25.dp),
                                        maxLines = 2,
                                        minLines = 1,
                                        style = TextStyle(lineBreak = LineBreak.Heading, textAlign = TextAlign.Center, color = LocalContentColor.current)
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
                                        text = rule.packageName,
                                        autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = titleMaxDimen, stepSize = 0.25.sp),
                                        modifier = Modifier
                                            .padding(vertical = 6.dp, horizontal = 10.dp),
                                        maxLines = 1,
                                        minLines = 1,
                                        style = TextStyle(lineBreak = LineBreak.Heading, textAlign = TextAlign.Center, color = LocalContentColor.current)
                                    )
                                }
                            }
                        } else {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.background,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp, horizontal = 10.dp)
                            ) {
                                BasicText(
                                    text = rule.appName,
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
                                    ExposedDropdownMenuAppRule(
                                        stringArrayResource(R.array.rule_conditions),
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
                                    ExposedDropdownMenuAppRule(
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
                                                true -> ExposedDropdownMenuAppRule(stringArrayResource(R.array.mute_unmute), viewModel, context, 0)
                                                false -> ExposedDropdownMenuAppRule(stringArrayResource(R.array.mute_unmute), viewModel, context, 1)
                                                else -> ExposedDropdownMenuAppRule(stringArrayResource(R.array.mute_unmute), viewModel, context, 0)
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
                                                        viewModel.updateAppRuleValue(number)
                                                        viewModel.updateAppRuleBool(null)
                                                    },
                                                    valueRange = minVolume.toFloat()..maxVolume.toFloat(),
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
