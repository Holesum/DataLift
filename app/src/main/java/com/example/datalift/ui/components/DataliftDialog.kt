package com.example.datalift.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.datalift.designsystem.theme.DataliftTheme
import com.example.datalift.screens.challenges.getStartOfNextDay
import com.example.datalift.screens.challenges.getStartOfTommorwTimetamp
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun StatelessDataliftBoxDialog(
    width: Dp,
    height: Dp,
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    color: Color = Color.White,
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
fun StatelessDataliftCloseCardDialog(
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
                Column {
                    content()
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun StatelessDataLiftCloseCardDialogPreview(){
    DataliftTheme {
        Surface {
            StatelessDataliftCloseCardDialog(
                isVisible = true,
                onDismissRequest = {}
            ){

            }
        }
    }
}

@Composable
fun StatelessDataliftTwoButtonDialog(
    height: Dp = 375.dp,
    padding: Dp = 16.dp,
    roundedCorners: Dp = 16.dp,
    isVisible: Boolean,
    buttonOneText: String = "Dismiss",
    buttonTwoText: String = "Confirm",
    onDismissRequest: () -> Unit,
    buttonOneAction: () -> Unit = onDismissRequest,
    buttonTwoAction: () -> Unit,
    content: @Composable () -> Unit,
){
    if(isVisible){
        Dialog(onDismissRequest = onDismissRequest) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .padding(padding),
                shape = RoundedCornerShape(roundedCorners)
            ) {
                Column {
                    content()
                    HorizontalDivider(
                        modifier = Modifier.height(4.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .align(Alignment.End)
                            .height(64.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(
                            onClick = buttonOneAction,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(text = buttonOneText)
                        }
                        TextButton(
                            onClick = buttonTwoAction,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(text = buttonTwoText)
                        }
                    }

                }
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
                Column {
                    content()
                    HorizontalDivider(
                        modifier = Modifier.height(4.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .align(Alignment.End)
                            .height(64.dp),
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (Long?, Long?) -> Unit,
    onDismiss: () -> Unit,
){
    val dateRangePickerState = rememberDateRangePickerState(
        selectableDates = FutureSelectableDate
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = isValidRange(
                    dateRangePickerState.selectedStartDateMillis,
                    dateRangePickerState.selectedEndDateMillis,
                ),
                onClick = {
                    onDateRangeSelected(
                        dateRangePickerState.selectedStartDateMillis,
                        dateRangePickerState.selectedEndDateMillis
                    )
                    onDismiss()
                }
            ) { 
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = "Select date range"
                )
            },
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private object FutureSelectableDate: SelectableDates{
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis >= getStartOfTommorwTimetamp()
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year >= currentYear()
    }
}

private fun currentYear(): Int {
    val zoneID = ZoneId.systemDefault()

    return LocalDate.now(zoneID)
        .year
}

private fun isValidRange(startDate: Long?, endDate: Long?): Boolean {
    if(startDate == null || endDate == null) return false

    val tomorrow = getStartOfTommorwTimetamp()
    val dayAfterTomorrow = getStartOfNextDay(tomorrow)
    return startDate >= tomorrow && endDate >= dayAfterTomorrow
}