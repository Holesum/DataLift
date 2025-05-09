package com.example.datalift.screens.challenges

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.datalift.designsystem.theme.DataliftTheme
import com.example.datalift.model.ExerciseItem
import com.example.datalift.model.Mgoal
import com.example.datalift.screens.profile.GoalCreationDialog
import com.example.datalift.ui.DevicePreviews
import com.example.datalift.ui.components.DataliftIcons
import com.example.datalift.ui.components.DateRangePickerModal
import com.example.datalift.ui.components.StatelessDataliftFormTextField
import com.google.firebase.Timestamp
import java.util.Date
import java.util.Locale

@Composable
fun ChallengeCreationScreen(
    challengeCreationViewModel: ChallengeCreationViewModel = hiltViewModel(),
    navUp: () -> Unit,
    navigateToChallengeFeed: () -> Unit
){
    val uiState by challengeCreationViewModel.uiState.collectAsStateWithLifecycle()
    val exercises by challengeCreationViewModel.exercises.collectAsStateWithLifecycle()

    ChallengeCreationScreen(
        title = uiState.title,
        description = uiState.description,
        goal = uiState.goal,
        canCreateChallenge = uiState.canCreateChallenge,
        startDate = uiState.startDate,
        endDate = uiState.endDate,
        createChallenge = challengeCreationViewModel::createChallenge,
        updateDateRange = challengeCreationViewModel::updateDates,
        updateGoal = challengeCreationViewModel::updateGoal,
        updateTitle = challengeCreationViewModel::updateTitle,
        updateDescription = challengeCreationViewModel::updateDescription,
        navUp = navUp,
        navigateToChallengeFeed = navigateToChallengeFeed,
        exerciseQuery = challengeCreationViewModel::getExercises,
        isImperial = challengeCreationViewModel.getUnitSystem(),
        exercises = exercises
    )
}

@Composable
private fun ChallengeCreationScreen(
    title: String,
    description: String,
    goal: Mgoal?,
    canCreateChallenge: Boolean,
    startDate: Long?,
    endDate: Long?,
    createChallenge: () -> Boolean,
    updateDateRange: (Long?, Long?) -> Unit,
    updateTitle: (String) -> Unit,
    updateDescription: (String) -> Unit,
    updateGoal: (Mgoal) -> Unit,
    navUp: () -> Unit = {},
    navigateToChallengeFeed: () -> Unit = {},
    exerciseQuery: (String) -> Unit,
    isImperial: Boolean = true,
    exercises: List<ExerciseItem>,
){
    var showDialog by remember { mutableStateOf(false) }
    var showDateDialog by remember { mutableStateOf(false) }


    Column() {
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = navUp) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
            Text(
                text = "Challenge Creation",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 16.dp)
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            thickness = 1.dp
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            StatelessDataliftFormTextField(
                field = "Challenge Title",
                text = title,
                changeText = updateTitle,
                modifier = Modifier.padding(top = 8.dp)
            )
            StatelessDataliftFormTextField(
                field = "Description",
                singleLine = false,
                text = description,
                changeText = updateDescription,
            )
            Row {
                StatelessDataliftFormTextField(
                    field = "Start Date",
                    text = ReturnDate(startDate),
                    readOnly = true,
                    changeText = {},
                    modifier = Modifier.weight(0.75f)
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = "-",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.padding(8.dp))
                StatelessDataliftFormTextField(
                    field = "End Date",
                    text = ReturnDate(endDate),
                    changeText = { _ -> },
                    readOnly = true,
                    modifier = Modifier.weight(0.75f)
                )
                IconButton(
                    onClick = { showDateDialog = true },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = DataliftIcons.Calendar,
                        contentDescription = null
                    )
                }
            }
            GoalCreationAlert(
                displayDialog = { showDialog = true },
                goal = goal,
                modifier = Modifier.padding(8.dp)
            )
            Button(
                onClick = {
                    if (createChallenge()){
                        navigateToChallengeFeed()
                    }

                },
                enabled = canCreateChallenge
            ) {
                Text("Create Challenge")
            }
        }
    }

    if(showDialog){
        GoalCreationDialog(
            onDismiss = {showDialog = false},
            onCreateGoal = updateGoal,
            getQuery = exerciseQuery,
            isImperial = isImperial,
            exercises = exercises
        )
    }

    if(showDateDialog){
        DateRangePickerModal(
            onDateRangeSelected = updateDateRange,
            onDismiss = { showDateDialog = false}
        )
    }
}

fun DateFormatted(time: Timestamp): String =
    SimpleDateFormat("MMM dd", Locale.getDefault())
        .format(time.toDate())

fun ReturnDate(time: Long?) : String{
    return if(time != null){
        DateFormatted(Timestamp(Date(time)))
    } else {
        ""
    }
}

@Composable
private fun GoalCreationAlert(
    displayDialog: () -> Unit,
    goal: Mgoal?,
    modifier: Modifier = Modifier
){
    Column(modifier = modifier) {
        Row {
            Column {
                Text(
                    text = "Goal",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if(goal != null) "Goal Selected" else "No goal selected",
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic
                ) // Or No goal attached
            }
            IconButton(
                onClick = displayDialog,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = DataliftIcons.Add,
                    contentDescription = null
                )
            }
        }
        // Error subtext
    }
}

@DevicePreviews
@Composable
fun ChallengeCreationScreenPreview(){
    DataliftTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            ChallengeCreationScreen(
                title = "",
                description = "",
                goal = null,
                startDate = null,
                endDate = null,
                createChallenge = { true },
                canCreateChallenge = false,
                updateDateRange = {_,_ ->},
                updateGoal = {_ ->},
                updateTitle = {_ ->},
                updateDescription = {_ ->},
                exerciseQuery = {_ ->},
                exercises = emptyList()
            )
        }
    }
}