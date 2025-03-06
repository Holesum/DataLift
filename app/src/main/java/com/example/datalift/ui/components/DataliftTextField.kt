package com.example.datalift.ui.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun DataliftNumberTextField(
    field: String,
    singleLine: Boolean = true,
    suffix: String = "",
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = { updateText -> text = updateText},
        label = { Text(text = field) },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        suffix = { if(suffix != ""){ Text(suffix) } },
        modifier = modifier
    )
}

@Composable
fun DataliftFormTextField(
    field: String,
    singleLine: Boolean = true,
    suffix: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = { updateText -> text = updateText},
        label = { Text(text = field) },
        singleLine = singleLine,
        suffix = { if(suffix != ""){ Text(suffix) } },
        keyboardOptions = keyboardOptions,
        modifier = modifier
    )
}

@Composable
fun StatelessDataliftNumberTextField(
    field: String,
    text: String,
    changeText: (String) -> Unit,
    singleLine: Boolean = true,
    suffix: String = "",
    isError: Boolean = false,
    supportingText: @Composable() (() -> Unit)? = null,
    trailingIcon: @Composable() (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = text,
        onValueChange = changeText,
        label = { Text(text = field) },
        singleLine = singleLine,
        isError = isError,
        supportingText = supportingText,
        suffix = { if(suffix != ""){ Text(suffix) } },
        trailingIcon = trailingIcon,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier
    )
}

@Composable
fun StatelessDataliftFormTextField(
    field: String,
    text: String,
    changeText: (String) -> Unit,
    singleLine: Boolean = true,
    suffix: String = "",
    isError: Boolean = false,
    supportingText: @Composable() (() -> Unit)? = null,
    trailingIcon: @Composable() (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {

    OutlinedTextField(
        value = text,
        onValueChange = changeText,
        label = { Text(text = field) },
        singleLine = singleLine,
        isError = isError,
        supportingText = supportingText,
        suffix = { if(suffix != ""){ Text(suffix) } },
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
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
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = modifier
    )
}

@Composable
fun StatelessDataliftFormPrivateTextField(
    field: String,
    text: String,
    isError: Boolean = false,
    supportingText: @Composable() (() -> Unit)? = null,
    changeText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var textVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = text,
        onValueChange = changeText,
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
        supportingText = supportingText,
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
    isError: Boolean = false,
    supportingText: @Composable() (() -> Unit)? = null,
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
        isError = isError,
        supportingText = supportingText,
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

@Composable
fun MenuFieldToModal(
    field: String,
    options: List<String> = emptyList(),
    modifier: Modifier = Modifier
) {
    val (savedOption, setSavedOption) = remember { mutableStateOf("") }
    var menuVisible by remember { mutableStateOf(false) }

    StatelessDataliftDialogTextField(
        field = field,
        text = savedOption,
        dialogVisible = menuVisible,
        setVisibile = { menuVisible = true},
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.padding(8.dp)
        ) {
            DropdownMenu(
                expanded = menuVisible,
                onDismissRequest = { menuVisible = false}
            ) {
                options.forEach{ option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            setSavedOption(option)
                            menuVisible = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RadioOptionFieldToModal(
    field: String,
    options: List<String> = emptyList(),
    modifier: Modifier = Modifier
){
    val (selectedOption, changeSelectedOption) = remember { mutableStateOf("") }
    val (savedOption, confirmSavedOption) = remember { mutableStateOf("") }
    var dialogVisible by remember { mutableStateOf(false) }

    StatelessDataliftDialogTextField(
        field = field,
        text = savedOption,
        dialogVisible = dialogVisible,
        setVisibile = { dialogVisible = true },
        modifier = modifier
    ) {
        StatelessDataliftCDCardDialog(
            height = 350.dp,
            isVisible = dialogVisible,
            onDismissRequest = {
                dialogVisible = false
                changeSelectedOption(savedOption)
            },
            onConfirmation = {
                confirmSavedOption(selectedOption)
                dialogVisible = false
            }
        ) {
            Column {
                Text(
                    text = field,
                    fontSize = 24.sp,
                    modifier = Modifier.fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 16.dp
                        )
                )
                HorizontalDivider(
                    modifier = Modifier.height(4.dp)
                )
                Column(
                    modifier = Modifier.selectableGroup()
                        .height(200.dp)
                        .verticalScroll(rememberScrollState())
                ){
                    options.forEach() { text ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .height(56.dp)
                                .selectable(
                                    selected = (text == selectedOption),
                                    onClick = { changeSelectedOption(text) },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = (text == selectedOption),
                                onClick = null // null recommended for accessibility with screen readers
                            )
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }

//                Box {
//                    LazyColumn {
//                        items(options){ text ->
//                            Row(
//                                modifier = Modifier.fillMaxWidth()
//                                    .height(56.dp)
//                                    .selectable(
//                                        selected = (text == selectedOption),
//                                        onClick = { changeSelectedOption(text) },
//                                        role = Role.RadioButton
//                                    )
//                                    .padding(horizontal = 16.dp),
//                                verticalAlignment = Alignment.CenterVertically,
//                            ) {
//                                RadioButton(
//                                    selected = (text == selectedOption),
//                                    onClick = null // null recommended for accessibility with screen readers
//                                )
//                                Text(
//                                    text = text,
//                                    style = MaterialTheme.typography.bodyLarge,
//                                    modifier = Modifier.padding(start = 16.dp)
//                                )
//                            }
//                        }
//                    }
//                }

//            }
        }
    }
}

@Composable
fun SemiStatelessRadioOptionFieldToModal(
    field: String,
    selectedOption: String,
    changeSelectedOption: (String) -> Unit,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    options: List<String> = emptyList(),
    modifier: Modifier = Modifier
){
//    val (selectedOption, changeSelectedOption) = remember { mutableStateOf("") }

    val (savedOption, confirmSavedOption) = remember { mutableStateOf("") }

//    if(selectedOption != ""){
//        confirmSavedOption(selectedOption)
//    }

    var dialogVisible by remember { mutableStateOf(false) }

    StatelessDataliftDialogTextField(
        field = field,
        text = selectedOption,
        dialogVisible = dialogVisible,
        isError = isError,
        supportingText = supportingText,
        setVisibile = { dialogVisible = true },
        modifier = modifier
    ) {
        StatelessDataliftCDCardDialog(
            height = 350.dp,
            isVisible = dialogVisible,
            onDismissRequest = {
                dialogVisible = false
                confirmSavedOption(selectedOption)
            },
            onConfirmation = {
                changeSelectedOption(savedOption)
                dialogVisible = false
            }
        ) {
            Column(
                modifier = Modifier.selectableGroup()
            ){
                Text(
                    text = field,
                    fontSize = 24.sp,
                    modifier = Modifier.fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 16.dp
                        )
                )
                HorizontalDivider(
                    modifier = Modifier.height(4.dp)
                )
                options.forEach() { text ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (text == savedOption),
                                onClick = { confirmSavedOption(text) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = (text == savedOption),
                            onClick = null // null recommended for accessibility with screen readers
                        )
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
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