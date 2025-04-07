package com.example.datalift.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.datalift.R
import com.example.datalift.ui.theme.DataliftTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataliftTopBar(
    modifier: Modifier = Modifier,
    title: String,
    onProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onFriendsClick: () -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(title)
        },
        actions = {
            IconButton(onClick = onFriendsClick) {
                Icon(
                    painter = painterResource(R.drawable.group),
                    contentDescription = null
                )
            }

            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null
                )
            }
        }
    )
}

@Preview
@Composable
fun DataliftTopBarPreview(){
    DataliftTheme {
        Surface {
            DataliftTopBar(
                title = "Hello"
            )
        }
    }
}