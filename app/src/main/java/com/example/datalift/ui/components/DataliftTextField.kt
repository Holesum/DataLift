package com.example.datalift.ui.components

import android.app.Dialog
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.datalift.R

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

@Composable
fun DataliftFormTextField(
    field: String,
    suffix: String = "",
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = { updateText -> text = updateText},
        label = { Text(text = field) },
        suffix = { if(suffix != ""){ Text(suffix) } },
        modifier = modifier
    )
}

@Composable
fun DataliftFormPrivateTextField(
    field: String,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }
    var textVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = text,
        onValueChange = { updateText -> text = updateText },
        visualTransformation =
            if(textVisible) VisualTransformation.None else PasswordVisualTransformation(),
        label = { Text(text = field) },
        trailingIcon = {
            val image = if(textVisible){
                    R.drawable.visibility
                } else { R.drawable.visibility_off }

            IconButton(onClick = {textVisible = !textVisible}) {
                Icon(
                    painter = painterResource(image),
                    contentDescription = "View text field"
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun DataliftDialogTextField(
    field:String,
    text: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
){
    var showDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = text,
        onValueChange = { },
        label = { Text(text = field) },
        trailingIcon = {
            Icon(Icons.Default.ArrowDropDown,contentDescription = null)
        },
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(text){
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if(upEvent != null){
                        showDialog = true
                    }
                }
            }
    )

    if(showDialog){
        content()
    }
}

@Composable
fun StatelessDataliftDialogTextField(
    field:String,
    text: String,
    dialogVisible: Boolean,
    setVisibile: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
){
    OutlinedTextField(
        value = text,
        onValueChange = { },
        label = { Text(text = field) },
        trailingIcon = {
            Icon(Icons.Default.ArrowDropDown,contentDescription = null)
        },
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(text){
                awaitEachGesture {

                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if(upEvent != null){
                        setVisibile()
                    }
                }
            }
    )

    if(dialogVisible){
        content()
    }
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