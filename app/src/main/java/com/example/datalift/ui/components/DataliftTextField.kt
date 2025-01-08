package com.example.datalift.ui.components

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun DataliftTextField(
    field: String,
    modifier: Modifier = Modifier
){
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { updateText -> text = updateText},
        label = { Text(text = field) },
        modifier = modifier
    )
}

//@Composable
//fun StatefulTextField(
//    modifier: Modifier = Modifier
//){
//    var text by remember { mutableStateOf("") }
//}
//
//@Composable
//fun StatelessTextField(
//    text: String,
//    textChange: (String) -> Unit
//)