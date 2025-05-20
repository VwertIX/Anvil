package com.example.anvil.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anvil.R
import com.example.anvil.ui.theme.primaryLight
import com.example.anvil.ui.theme.readmeSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadmeLocationScaffold() {

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
                        Text(stringResource(R.string.readme), fontSize = 30.sp)
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
        LazyColumn(contentPadding = innerPadding) {
            item {
                Column(
                    modifier = Modifier
                    .padding(20.dp)
                ) {
                    Text(stringResource(R.string.location_readme1), fontSize = readmeSize)
                    Spacer(Modifier.size(30.dp))
                    Text(stringResource(R.string.location_readme2), fontSize = readmeSize)
                    Spacer(Modifier.size(30.dp))
                    Text(stringResource(R.string.location_readme3), fontSize = readmeSize)
                    Spacer(Modifier.size(30.dp))
                    Text(stringResource(R.string.location_readme4), fontSize = readmeSize)
                }
            }
        }
    }

}