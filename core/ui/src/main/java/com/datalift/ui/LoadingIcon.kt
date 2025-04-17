package com.datalift.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LoadingIcon(
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
fun LoadingIconPreview(){
    MaterialTheme {
        Surface {
            LoadingIcon()
        }
    }
}