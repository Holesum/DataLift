package com.example.datalift.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.datalift.ui.DevicePreviews
import com.example.datalift.ui.theme.DataliftTheme
import dagger.hilt.android.lifecycle.HiltViewModel

@Composable
fun SettingsEntry(
    entry: String,
    subtext: String? = null,
    action: () -> Unit = {},
    modifier: Modifier = Modifier
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
        .fillMaxWidth()
        .height(64.dp)
        .padding(4.dp)
        .clickable(onClick = action)
    ){
        Column(
            modifier.padding(start = 10.dp)
        ) {
            Text(
                text = entry,
                fontWeight = FontWeight.Bold,
            )

            if (subtext != null) {
                Text(subtext)
            }
        }
    }

}

@Composable
fun SettingsRoute(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
){
    SettingsScreen(onBackClick)
}

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = modifier.fillMaxWidth()) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
            Text(
                text = "Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterVertically)
                    .padding(start = 16.dp)
            )
        }
        HorizontalDivider(
            modifier = modifier.padding(top = 8.dp),
            thickness = 1.dp
        )
        SettingsEntry(
            entry = "Units",
            subtext = "Imperial",
        )
        SettingsEntry(
            entry = "Privacy Controls",
            subtext = "Private",
        )
        SettingsEntry(
            entry = "Log out"
        )
    }
}

@DevicePreviews
@Composable
fun SettingsScreenPreview(){
    DataliftTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SettingsScreen(
                onBackClick = {}
            )
        }
    }
}