package com.example.datalift.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun StatelessDataliftBoxDialog(
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

@Composable
fun StatelessDataliftCardDialog(
    height: Dp = 375.dp,
    padding: Dp = 16.dp,
    roundedCorners: Dp = 16.dp,
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (isVisible){
        Dialog(
            onDismissRequest = { onDismissRequest() }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .padding(padding),
                shape = RoundedCornerShape(roundedCorners)
            ) {
                content()
            }
        }
    }
}

@Composable
fun StatelessDataliftCDCardDialog(
    height: Dp = 375.dp,
    padding: Dp = 16.dp,
    roundedCorners: Dp = 16.dp,
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (isVisible){
        Dialog(
            onDismissRequest = { onDismissRequest() }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .padding(padding),
                shape = RoundedCornerShape(roundedCorners)
            ) {
                content()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = { onConfirmation() },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}