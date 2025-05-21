package com.example.anvil.ui.pages

import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavHostController
import com.example.anvil.AddRuleScreen
import com.example.anvil.R
import com.example.anvil.data.AppInfo
import com.example.anvil.ui.AnvilViewModel
import com.example.anvil.ui.theme.primaryLight
import com.example.anvil.ui.theme.titleMaxDimen
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectAppScaffold(navController: NavHostController, viewModel: AnvilViewModel, packageList: AppInfo) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    titleContentColor = primaryLight,
                ),
                title = {
                    Row(
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                    ) {
                        Column {
                            Text(stringResource(R.string.select_an_application), fontSize = 20.sp)


                            Switch(
                                modifier = Modifier.padding(top = 5.dp, bottom = 10.dp),
                                checked = viewModel.switch.collectAsStateWithLifecycle().value,
                                onCheckedChange = { viewModel.updateSwitch(it) }
                            )
                        }


                        Spacer(modifier = Modifier.size(10.dp))

                        TextField(
                            value = viewModel.searchText,
                            onValueChange = { viewModel.updateSearchText(it) },
                            placeholder = { Text(stringResource(R.string.search)) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(bottom = 4.dp)
                        )

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

        packageList.getPackages()

        AppList(packageList, innerPadding, navController, viewModel)
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppList(packageList: AppInfo, innerPadding: PaddingValues = PaddingValues(8.dp), navController: NavHostController, viewModel: AnvilViewModel) {

    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {




        LazyColumn(contentPadding = innerPadding) {
            items(packageList.installedPackages) { packageInfo ->

                val iconId: Int = packageList.getIconId(packageInfo)

                val packageName: String = packageList.getPackageName(packageInfo)
                val appName: String = packageList.getAppName(packageInfo)


                val switch by viewModel.switch.collectAsStateWithLifecycle()

                if (((iconId != 0 && switch == true) || (iconId == 0 && switch == false)) && (packageName.contains(viewModel.searchText.toString(), ignoreCase = true) || appName.contains(viewModel.searchText.toString(), ignoreCase = true))
                ) {
                    AppSelectionCard(packageInfo, packageList, navController, viewModel)
                }





            }
        }

    }

}



@Composable
fun AppSelectionCard(packageInfo: PackageInfo, packageList: AppInfo, navController: NavHostController, viewModel: AnvilViewModel) {
    val appName: String = packageList.getAppName(packageInfo)
    val packageName: String = packageList.getPackageName(packageInfo)
    val launcherIcon: Drawable = packageList.getPackageIcon(packageInfo)


    OutlinedCard(
        colors = CardDefaults.cardColors( containerColor = MaterialTheme.colorScheme.onPrimary ),
        border = BorderStroke(4.dp, MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier
            .padding(vertical = 6.dp, horizontal = 6.dp),
        onClick = dropUnlessResumed {
            viewModel.updateAppRuleAppName(appName)
            viewModel.updateAppRulePackageName(packageName)
            navController.navigate(AddRuleScreen)
        }
    ) {
        Row(
            modifier = Modifier
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
            if (appName != packageName) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
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
                            text = appName,
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
                            text = packageName,
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
                        .padding(vertical = 6.dp, horizontal = 10.dp)
                ) {

                    BasicText(
                        text = packageName,
                        autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = titleMaxDimen, stepSize = 0.25.sp),
                        modifier = Modifier
                            .padding(vertical = 6.dp, horizontal = 10.dp),
                        maxLines = 1,
                        minLines = 1,
                        style = TextStyle(lineBreak = LineBreak.Heading, textAlign = TextAlign.Center, color = LocalContentColor.current)
                    )
                }
            }
        }
    }
}
