package com.example.datalift.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.datalift.navigation.SettingDetail
import com.example.datalift.ui.DevicePreviews
import com.example.datalift.ui.theme.DataliftTheme
import dagger.hilt.android.lifecycle.HiltViewModel

@Composable
fun SettingsDialogRow(
    text: String,
    selectRow: (String) -> Unit,
    selected: Boolean

){
    Row(
        modifier = Modifier.fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = { selectRow(text) },
                role = Role.RadioButton
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            onClick = { selectRow(text) },
            selected = selected
        )
        Text(
            text = text,
            fontSize = 24.sp,
        )
    }
}

@Composable
fun SettingsDialogColumn(
    choice: String,
    updateChoice: (String) -> Unit,
    options: List<String>
) {
    Column(
        modifier = Modifier.selectableGroup()
            .verticalScroll(rememberScrollState())
    ){
        options.forEach { row ->
            SettingsDialogRow(
                text = row,
                selectRow = updateChoice,
                selected = choice == row
            )
        }
    }
}

@Composable
fun SettingsDialogScreen(
    setting: SettingDetail,
    navUp: () -> Unit,
    choice: String,
    updateChoice: (String) -> Unit,
){

    SettingsDialogScreen(
        navUp = navUp,
        title = setting.title,
        options = setting.options,
        choice = choice,
        updateChoice = updateChoice,

    )
}

@Composable
internal fun SettingsDialogScreen(
    navUp: () -> Unit,
    title: String,
    choice: String,
    updateChoice: (String) -> Unit,
    options: List<String>,
    modifier: Modifier = Modifier
){
    Column {
        Row(modifier = modifier.fillMaxWidth()) {
            IconButton(onClick = navUp) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
            Text(
                text = title,
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
        SettingsDialogColumn(
            choice = choice,
            updateChoice = updateChoice,
            options = options
        )
    }
}

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
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    navigateToDetail: (SettingDetail) -> Unit,
    onBackClick: () -> Unit,
){
    val uiState = settingsViewModel.settingsUiState.collectAsStateWithLifecycle().value

    SettingsScreen(
        onBackClick = onBackClick,
        navigateToDetail = navigateToDetail,
        privacy = uiState.privacy,
        units = uiState.units,
        updatePrivacy = settingsViewModel::updatePrivacy,
        updateUnits = settingsViewModel::updateUnit
    )
}

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    navigateToDetail: (SettingDetail) -> Unit,
    privacy: String,
    units: String,
    updatePrivacy: (String) -> Unit,
    updateUnits: (String) -> Unit,
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
            subtext = units,
            action = {
                navigateToDetail(
                    SettingDetail(
                        title = "Units",
                        options = listOf("Imperial","Metric"),
//                        currentChoice = units,
                        type = SettingsType.UNITS
                    )
                )
            }
        )
        SettingsEntry(
            entry = "Privacy Controls",
            subtext = privacy,
            action = {
                navigateToDetail(
                    SettingDetail(
                        title = "Privacy",
                        options = listOf("Public","Followers Only","Private"),
//                        currentChoice = privacy,
                        type = SettingsType.PRIVACY
                    )
                )
            }
        )
        SettingsEntry(
            entry = "Log out"
        )
    }
}

@Preview
@Composable
fun DialogScreenPreview(){
    DataliftTheme {
        Surface() {
            SettingsDialogColumn(
                choice = "Imperial",
                updateChoice = {_ -> },
                options = listOf("Imperial","Metric")
            )
        }
    }
}

@DevicePreviews
@Composable
fun SettingsScreenPreview(){
    DataliftTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SettingsScreen(
                onBackClick = {},
                navigateToDetail = {_ -> },
                privacy = "Private",
                units = "Imperial",
                updatePrivacy = {_ -> },
                updateUnits = {_ -> },
            )
        }
    }
}

