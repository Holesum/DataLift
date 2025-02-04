package com.example.datalift.screens.workout

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.datalift.R
import com.example.datalift.model.Mexercise
import com.example.datalift.model.Mset
import com.example.datalift.model.Mworkout
import com.example.datalift.ui.components.StatelessDataliftFormTextField

@Composable
fun WorkoutDetailsScreen(
    workoutViewModel: WorkoutViewModel = viewModel(),
    modifier: Modifier = Modifier,
    navUp: () -> Unit = {},
    navNext: () -> Unit = {}
) {
    var workout: Mworkout = Mworkout()
    var isExerciseDialogVisible by remember { mutableStateOf(false) }
    var isAddSetVisible by remember { mutableStateOf(false) }
    var selectedExercise by remember { mutableStateOf<Mexercise?>(null) }
    var isWorkoutAdded by remember { mutableStateOf(false) }
    var saveWorkout by remember { mutableStateOf(true) }

    if(workoutViewModel.workout.collectAsState().value != null) {
        workout = workoutViewModel.workout.collectAsState().value!!
    }

    Column(
        modifier = Modifier.fillMaxHeight().fillMaxWidth(1F),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Workout: ${workout.name}", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Date: ${workout.getFormattedDate()}")
        Text(text = "Muscle Group: ${workout.muscleGroup}")

        // List exercises associated with the workout
        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
            items(workout.exercises) { exercise ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = exercise.name)
                    exercise.sets.forEach { set ->
                        Text(text = set.getFormattedSet())
                    }
                    Button(
                        onClick = {
                            selectedExercise = exercise
                            isAddSetVisible = true}
                    ) {
                        Text("Add Set")
                    }
                }
            }
        }
        // Button to open the exercise dialog
        Button(
            onClick = { isExerciseDialogVisible = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add Exercise")
        }
        if (isAddSetVisible){
            AddSetDialog(
                workoutViewModel = workoutViewModel,
                onDismiss = { isAddSetVisible = false },
                onAddSet = { newSet ->
                    selectedExercise?.sets = selectedExercise?.sets?.plus(newSet)!!
                    isAddSetVisible = false },
                modifier = Modifier.fillMaxHeight(0.5F)
            )
            saveWorkout = false
        } else {
            saveWorkout = true
        }
        Spacer(modifier = Modifier.weight(1F))

        if(saveWorkout) {
            Button(
                onClick = { workoutViewModel.createNewWorkout(workout); isWorkoutAdded = true },
                modifier = Modifier
            ) {
                Text("Save Workout")
            }
        }



    }

    // Show dialog to search and add exercise
    if (isExerciseDialogVisible) {
        SearchExerciseDialog(
            onDismiss = { isExerciseDialogVisible = false },
            onSelectExercise = { exercise ->
                workout.exercises += exercise
                isExerciseDialogVisible = false
            }
        )
    }
    if(isWorkoutAdded){
        isWorkoutAdded = false
        navNext()
    }
}

@Composable
fun AddSetDialog(
    onDismiss: () -> Unit,
    onAddSet: (Mset) -> Unit,
    workoutViewModel: WorkoutViewModel,
    modifier: Modifier = Modifier
){
    var weight by remember { mutableDoubleStateOf(0.0) }
    var reps by remember { mutableLongStateOf(0) }
    Column {
        StatelessDataliftFormTextField(
            field = "Weight",
            text = weight.toString(),
            changeText = { weight = it.toDouble() },
            modifier = Modifier.fillMaxWidth(0.75f)
        )
        StatelessDataliftFormTextField(
            field = "Reps",
            text = reps.toString(),
            changeText = { reps = if(it.isNotBlank()){
                it.toLong()
            } else {
                0
            } },
            modifier = Modifier.fillMaxWidth(0.75f)
        )
        Button(
            onClick = { onAddSet(Mset(reps, weight)) }) {
            Text("Confirm Set")
        }
        Row {
            IconButton(
                onClick = {}
            ){
                Image(painter = painterResource(R.drawable.weight_plate),
                    contentDescription = null)
            }
            IconButton(
                onClick = {}
            ){
                Image(painter = painterResource(R.drawable.weight_plate),
                    contentDescription = null,)
            }
            IconButton(
                onClick = {}
            ){
                Image(painter = painterResource(R.drawable.weight_plate),
                    contentDescription = null)
            }
            IconButton(
                onClick = {}
            ){
                Image(painter = painterResource(R.drawable.weight_plate),
                    contentDescription = null)
            }
            IconButton(
                onClick = {}
            ){
                Image(painter = painterResource(R.drawable.weight_plate),
                    contentDescription = null)
            }
        }
    }
}