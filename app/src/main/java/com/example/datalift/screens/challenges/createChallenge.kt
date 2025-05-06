package com.example.datalift.screens.challenges

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.datalift.designsystem.theme.DataliftTheme
import com.example.datalift.model.ChallengeProgress
import com.example.datalift.model.ExerciseItem
import com.example.datalift.model.GoalType
import com.example.datalift.model.Mchallenge
import com.example.datalift.model.Mgoal
import com.example.datalift.model.Muser
import com.example.datalift.model.smallTestChallenge
import com.example.datalift.model.testChallenge
import com.example.datalift.screens.profile.LoadingIcon
import com.example.datalift.screens.profile.SearchExerciseDialog
import com.example.datalift.ui.DevicePreviews
import com.example.datalift.ui.components.SemiStatelessRadioOptionFieldToModal
import com.example.datalift.ui.components.StatelessDataliftNumberTextField
import com.google.android.play.core.integrity.i
import com.google.firebase.Timestamp
import java.time.Duration
import java.util.Date
import java.util.Locale
import kotlin.math.abs


@Composable
fun CreateChallengeScreen(
    onChallengeCreated: () -> Unit,
    exercises: List<ExerciseItem>,
    onCreateGoal: (Mgoal) -> Unit,
    getQuery: (String) -> Unit,
    isImperial: Boolean,
    currUserID: String,
    uiState: ChallengesUiState,

){
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(Date()) }
    var endDate by remember { mutableStateOf(Date()) }
    var selectedGoal by remember { mutableStateOf<Mgoal?>(null) }
    var isGoalDialogVisible by remember { mutableStateOf(false) }

    //build challenge object in screen
    var challenge = Mchallenge()
    //First select the goal of the challenge
        //prebuilt just call the SelectGoalDialog
    //choose title, description
    //add in other users
        //Steal lookup from profile screen
    //save challenge

}


/*
    exercises: List<ExerciseItem>,
    getQuery: (String) -> Unit,
    onAddGoalClicked = { profileViewModel.toggleDialogVisibility() },
    createGoal = { goal: Mgoal -> profileViewModel.createGoal(goal)
                 profileViewModel.hideDialog()},
    exercises = exercises,
    getQuery = profileViewModel::getExercises,
    isDialogVisible = isDialogVisible,
    removeGoal = profileViewModel::deleteGoal,
    isImperial = profileViewModel.getUnitSystem(),
    isCurrentUser = profileViewModel.isCurrUser()
 */
@Composable
fun SelectGoalDialog(
    onDismiss: () -> Unit,
    onCreateGoal: (Mgoal) -> Unit,
    exercises: List<ExerciseItem>,
    getQuery: (String) -> Unit,
    isImperial: Boolean
) {
    var selectedType by remember { mutableStateOf(GoalType.UNKNOWN) }
    var targetValue by remember { mutableStateOf("") }
    var percentage by remember { mutableStateOf("") }
    var bodyPart by remember { mutableStateOf("") }
    var exerciseName by remember { mutableStateOf("") }

    var search by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SemiStatelessRadioOptionFieldToModal(
                    field = "Goal Type",
                    selectedOption = selectedType.toDisplayString(),
                    changeSelectedOption = { selectedString ->
                        selectedType = GoalType.entries
                            .firstOrNull { it.toDisplayString() == selectedString }
                            ?: GoalType.UNKNOWN
                    },
                    options = GoalType.entries
                        .filter { it != GoalType.UNKNOWN }
                        .map { it.toDisplayString() },
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(0.75f)
                )


                when (selectedType) {
                    GoalType.INCREASE_ORM_BY_VALUE -> {
                        if(search){
                            SearchExerciseDialog({search = false}, { exercise -> exerciseName = exercise.name; search = false}, exercises, getQuery )
                        }
                        Text("Exercise Name: $exerciseName")
                        Button(onClick = {search = true}){
                            Text("Change Exercise")
                        }
                        //use statelessdataliftnumberfield
                        if(isImperial) {
                            StatelessDataliftNumberTextField(
                                field = "Increase Weight by (lb)",
                                suffix = "lbs",
                                text = targetValue,
                                changeText = { targetValue = it },
                                isError = false
                            )
                        } else {
                            StatelessDataliftNumberTextField(
                                field = "Increase Weight by (kg)",
                                suffix = "kgs",
                                text = targetValue,
                                changeText = { targetValue = it },
                                isError = false
                            )
                        }
                    }

                    GoalType.INCREASE_ORM_BY_PERCENTAGE -> {
                        if(search){
                            SearchExerciseDialog({search = false}, { exercise -> exerciseName = exercise.name; search = false}, exercises, getQuery )
                        }
                        Text("Exercise Name: $exerciseName")
                        Button(onClick = {search = true}){
                            Text("Change Exercise")
                        }
                        OutlinedTextField(
                            value = percentage,
                            onValueChange = { percentage = it },
                            label = { Text("Target Increase (%)") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )
                    }

                    GoalType.COMPLETE_X_WORKOUTS, GoalType.COMPLETE_X_WORKOUTS_OF_BODY_PART -> {
                        if (selectedType == GoalType.COMPLETE_X_WORKOUTS_OF_BODY_PART) {
                            OutlinedTextField(
                                value = bodyPart,
                                onValueChange = { bodyPart = it },
                                label = { Text("Body Part") }
                            )
                        }
                        OutlinedTextField(
                            value = targetValue,
                            onValueChange = { targetValue = it },
                            label = { Text("Target Workouts") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )
                    }

                    GoalType.COMPLETE_X_REPS_OF_EXERCISE -> {
                        if(search){
                            SearchExerciseDialog({search = false}, { exercise -> exerciseName = exercise.name; search = false}, exercises, getQuery )
                        }
                        Text("Exercise Name: $exerciseName")
                        Button(onClick = {search = true}){
                            Text("Change Exercise")
                        }
                        OutlinedTextField(
                            value = targetValue,
                            onValueChange = { targetValue = it },
                            label = { Text("Number of Reps") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )
                    }

                    else -> Unit
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val goal = when (selectedType) {
                        GoalType.INCREASE_ORM_BY_VALUE ->
                            if(isImperial){
                                Mgoal(
                                    type = selectedType,
                                    exerciseName = exerciseName,
                                    targetValue = targetValue.toIntOrNull() ?: 0
                                )
                            } else {
                                Mgoal(
                                    type = selectedType,
                                    exerciseName = exerciseName,
                                    targetValue = (targetValue.toDouble() * 2.20462).toInt()
                                )
                            }

                        GoalType.INCREASE_ORM_BY_PERCENTAGE -> Mgoal(
                            type = selectedType,
                            exerciseName = exerciseName,
                            targetPercentage = percentage.toDoubleOrNull() ?: 0.0
                        )

                        GoalType.COMPLETE_X_WORKOUTS -> Mgoal(
                            type = selectedType,
                            targetValue = targetValue.toIntOrNull() ?: 0
                        )

                        GoalType.COMPLETE_X_WORKOUTS_OF_BODY_PART -> Mgoal(
                            type = selectedType,
                            bodyPart = bodyPart,
                            targetValue = targetValue.toIntOrNull() ?: 0
                        )

                        GoalType.COMPLETE_X_REPS_OF_EXERCISE -> Mgoal(
                            type = selectedType,
                            exerciseName = exerciseName,
                            targetValue = targetValue.toIntOrNull() ?: 0
                        )

                        else -> null
                    }

                    goal?.let { onCreateGoal(it)     //Todo change code here to set a goal object in the challenge ViewModel
                        onDismiss()
                    }
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun GoalType.toDisplayString(): String =
    name.lowercase()
        .replace("_", " ")
        .replaceFirstChar { it.uppercase() }

// Optional reverse function if needed
fun String.toGoalType(): GoalType =
    GoalType.entries.first { it.name.equals(this.replace(" ", "_"), ignoreCase = true) }

