package com.example.anvil.ui.pages

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavHostController
import com.example.anvil.R
import com.example.anvil.SelectAppScreen
import com.example.anvil.SelectLocationScreen
import com.example.anvil.ui.AnvilViewModel
import com.example.anvil.ui.theme.primaryLight
import com.example.anvil.ui.theme.titleMaxDimen
import com.google.android.gms.location.FusedLocationProviderClient


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectRuleScaffold(context: Context, navController: NavHostController, viewModel: AnvilViewModel, locationPermissionRequest: ActivityResultLauncher<Array<String>>, fusedLocationClient: FusedLocationProviderClient) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = primaryLight,
                ),
                title = {
                        Text(stringResource(R.string.choose_a_rule_type), fontSize = 30.sp)
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

        LazyColumn(
            contentPadding = innerPadding,
        modifier = Modifier
            .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            item {


                OutlinedCard(
                    colors = CardDefaults.cardColors( containerColor = MaterialTheme.colorScheme.onPrimary ),
                    border = BorderStroke(4.dp, MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier
                        .height(250.dp)
                        .padding(vertical = 6.dp, horizontal = 10.dp),
                    onClick = dropUnlessResumed {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            viewModel.fetchUserLocation(context, fusedLocationClient)
                            navController.navigate(SelectLocationScreen)
                        } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            viewModel.fetchUserLocation(context, fusedLocationClient)
                            navController.navigate(SelectLocationScreen)
                        }
                        locationPermissionRequest.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                        locationPermissionRequest.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            )
                        )

                    }
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxHeight()
                        ) {
                        BasicText(
                            text = "Location Rule",
                            autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = titleMaxDimen, stepSize = 0.25.sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 25.dp),
                            maxLines = 2,
                            minLines = 1,
                            style = TextStyle(lineBreak = LineBreak.Heading, textAlign = TextAlign.Center, color = LocalContentColor.current))
                    }
                }
            }
            item {
                OutlinedCard(
                    colors = CardDefaults.cardColors( containerColor = MaterialTheme.colorScheme.onPrimary ),
                    border = BorderStroke(4.dp, MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier
                        .height(250.dp)
                        .padding(vertical = 6.dp, horizontal = 10.dp),
                    onClick = dropUnlessResumed {

                        navController.navigate(SelectAppScreen)
                    }
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxHeight()
                    ) {

                        BasicText(
                            text = "App Rule",
                            autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = titleMaxDimen, stepSize = 0.25.sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 25.dp),
                            maxLines = 2,
                            minLines = 1,
                            style = TextStyle(lineBreak = LineBreak.Heading, textAlign = TextAlign.Center, color = LocalContentColor.current)
                        )
                    }
                }
            }
        }
    }
}