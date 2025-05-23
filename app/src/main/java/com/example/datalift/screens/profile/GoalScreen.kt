package com.example.datalift.screens.profile

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.datalift.model.ExerciseItem
import com.example.datalift.model.GoalType
import com.example.datalift.model.Mexercise
import com.example.datalift.model.Mgoal
import com.example.datalift.ui.components.SemiStatelessRadioOptionFieldToModal
import com.example.datalift.ui.components.StatelessDataliftNumberTextField
import com.example.datalift.utils.toDisplayWeight

@Composable
fun GoalSection(
    isVisibile: Boolean = true,
    goals: List<Mgoal>,
    isDialogVisible: Boolean = false,
    onAddGoalClicked: () -> Unit,
    createGoal: (Mgoal) -> Unit,
    exercises: List<ExerciseItem>,
    getQuery: (String) -> Unit,
    removeGoal: (Mgoal) -> Unit,
    isImperial: Boolean
) {
    if(isVisibile) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Your Goals", style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = onAddGoalClicked) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Goal")
                }
            }

            if (goals.isEmpty()) {
                Text(
                    "No goals yet. Add one to start tracking!",
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                LazyColumn() {
                    items(goals) { goal ->
                        Column {
                            Row { GoalCard(goal = goal, isImperial) }
                            IconButton(
                                onClick = { removeGoal(goal) },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Workout")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
            if (isDialogVisible) {
                GoalCreationDialog(
                    onDismiss = onAddGoalClicked,
                    onCreateGoal = { goal ->
                        createGoal(goal)
                        onAddGoalClicked() // Close dialog after goal creation
                    },
                    exercises = exercises,
                    getQuery = getQuery,
                    isImperial = isImperial
                )
            }
        }
    }
}

@Composable
fun GoalCard(
    goal: Mgoal,
    isImperial: Boolean
) {
    Log.d("test", "GoalCard called $goal")

    val title = when (goal.type) {
        GoalType.INCREASE_ORM_BY_PERCENTAGE ->
            "Increase ${goal.exerciseName} one rep max by ${goal.targetPercentage?.toInt()}%"

        GoalType.INCREASE_ORM_BY_VALUE -> {
            val unit = if (isImperial) "lbs" else "kgs"
            val target = goal.targetValue.toDisplayWeight(isImperial)
            "Increase ${goal.exerciseName} one rep max to $target $unit"
        }

        GoalType.COMPLETE_X_WORKOUTS ->
            "Complete ${goal.trueTargetValue} workouts"

        GoalType.COMPLETE_X_WORKOUTS_OF_BODY_PART ->
            "Do ${goal.trueTargetValue} ${goal.bodyPart} workouts"

        GoalType.COMPLETE_X_REPS_OF_EXERCISE ->
            "Do ${goal.trueTargetValue} reps of ${goal.exerciseName}"

        else -> "Unknown Goal"
    }
    Log.d("Goaltest", "goal: $goal")
    val progressText = when {
        goal.isComplete -> "✅ Completed!"
        goal.type == GoalType.INCREASE_ORM_BY_VALUE -> {
            val current = goal.currentValue.toDisplayWeight(isImperial)
            val target = goal.targetValue.toDisplayWeight(isImperial)
            "$current / $target"
        }
        goal.type == GoalType.INCREASE_ORM_BY_PERCENTAGE ->
            "${goal.currentValue - 100}% / ${goal.targetPercentage?.toInt()}%"

        goal.type == GoalType.COMPLETE_X_WORKOUTS ->
            "${goal.currentValue} / ${goal.trueTargetValue}"

        goal.type == GoalType.COMPLETE_X_REPS_OF_EXERCISE ->
            "${goal.targetValue - goal.currentValue - goal.trueTargetValue} / ${goal.trueTargetValue}"


        else ->
             "${goal.currentValue} / ${goal.trueTargetValue}"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (goal.isComplete) Color(0xFFD0F0C0) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = progressText,
                color = if (goal.isComplete) Color.Green else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun GoalCreationDialog(
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
                            SearchExerciseDialog(
                                {search = false},
                                {exercise -> exerciseName = exercise.name; search = false},
                                exercises,
                                getQuery )
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
                        if(search){SearchExerciseDialog({search = false}, {exercise -> exerciseName = exercise.name; search = false}, exercises, getQuery )}
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
                        if(search){SearchExerciseDialog({search = false}, {exercise -> exerciseName = exercise.name; search = false}, exercises, getQuery )}
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

                    goal?.let { onCreateGoal(it)
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

//@Composable
//fun DropdownMenuGoalType(
//    selectedType: GoalType,
//    onTypeSelected: (GoalType) -> Unit
//) {
//    var expanded by remember { mutableStateOf(false) }
//
////    Box {
////        SemiStatelessRadioOptionFieldToModal(
////            field = "Goal Type",
////            selectedOption = exerciseName,
////            changeSelectedOption = updateExercise,
////            options = exerciseNames,
////            modifier = modifier.padding(4.dp).fillMaxWidth(0.75f)
////        )
////        OutlinedTextField(
////            value = selectedType.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
////            onValueChange = {},
////            readOnly = true,
////            label = { Text("Goal Type") },
////            modifier = Modifier
////                .fillMaxWidth()
////                .clickable { expanded = true }
////        )
////
////        DropdownMenu(
////            expanded = expanded,
////            onDismissRequest = { expanded = false }
////        ) {
////            GoalType.entries
////                .filter { it != GoalType.UNKNOWN }
////                .forEach { type ->
////                    DropdownMenuItem(
////                        text = { Text(type.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) },
////                        onClick = {
////                            onTypeSelected(type)
////                            expanded = false
////                        }
////                    )
////                }
////        }
//    }
//}

@Composable
fun SearchExerciseDialog(
    onDismiss: () -> Unit,
    onSelectExercise: (Mexercise) -> Unit,
    exercises: List<ExerciseItem>,
    getQuery: (String) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    // Search query changes trigger fetching exercises
    LaunchedEffect(query) {
        val handler = Handler(Looper.getMainLooper())
        var runnable: Runnable? = null

        runnable?.let { handler.removeCallbacks(it) }

        runnable = Runnable {
            // Trigger the exercise search after the delay
            getQuery(query)
        }

        handler.postDelayed(runnable, 500) // Delay for 500ms before fetching exercises
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Search Exercises") },
        text = {
            Column(modifier = Modifier.fillMaxHeight(0.8f)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Search Exercise") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(text = "Search results:")

                // Show search results
                LazyColumn {
                    items(exercises) { exercise ->
                        Text(
                            text = exercise.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val exer = Mexercise(
                                        name = exercise.title,
                                        exercise = exercise,
                                        sets = emptyList()
                                    )
                                    onSelectExercise(exer) // Select an exercise and add it to the workout
                                }
                                .padding(8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}