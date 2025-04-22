package com.datalift.designsystem.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DataliftLoadingIcon(
    contentDesc: String,
    modifier: Modifier = Modifier
){
    CircularProgressIndicator(
        modifier = modifier
            .size(128.dp),
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
    )
}

@Preview
@Composable
fun DataliftLoadingIconPreview(){
    MaterialTheme {
        Surface {
            DataliftLoadingIcon(contentDesc = "LoadingIcon")
        }
    }
}