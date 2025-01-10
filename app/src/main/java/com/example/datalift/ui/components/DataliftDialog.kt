package com.example.datalift.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun StatelessDataliftDialog(
    width: Dp,
    height: Dp,
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    color: Color = Color.White,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (isVisible){
        Dialog(
            onDismissRequest = { onDismissRequest() }
        ) {
            Box(
                modifier = Modifier.size(width,height)
                    .background(color)
            ){
                content()
            }
        }
    }

}