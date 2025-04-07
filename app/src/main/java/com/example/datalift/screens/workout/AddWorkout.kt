package com.example.datalift.screens.workout

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.datalift.R
import com.example.datalift.model.Mexercise
import com.example.datalift.model.Mset
import com.example.datalift.model.Mworkout
import com.example.datalift.ui.components.StatelessDataliftFormTextField
import com.example.datalift.ui.theme.DataliftTheme


@Composable
fun WorkoutDetailsScreen(
    workoutViewModel: WorkoutViewModel = hiltViewModel(),
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
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(1F),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isAddSetVisible) {
            Text(text = "Workout: ${workout.name}", style = MaterialTheme.typography.headlineMedium)
            Text(text = "Date: ${workout.getFormattedDate()}")
            Text(text = "Muscle Group: ${workout.muscleGroup}")

            // List exercises associated with the workout
            LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                items(workout.exercises) { exercise ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.95f) // Adjust width as needed
                            .padding(8.dp, 8.dp) // Optional: Add spacing between exercises
                            .clip(MaterialTheme.shapes.medium) // Apply rounded corners
                            .background(MaterialTheme.colorScheme.surface), // Background color
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = exercise.name,
                                modifier = Modifier.padding(5.dp).weight(1f)
                            )
                            // Edit button to re-show Add Set under this exercise
                            IconButton(
                                onClick = {
                                    selectedExercise = exercise
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit, // Pencil icon
                                    contentDescription = "Edit Exercise"
                                )
                            }
                        }

                        // Show Add Set button only under the selected exercise
                        if (selectedExercise == exercise) {
                            exercise.sets.forEach { set ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = set.getFormattedSet(),
                                        modifier = Modifier.weight(1f))
                                    IconButton(onClick = {workoutViewModel.removeSet(exercise, set)}) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete set")
                                    }

                                }
                            }
                            Button(
                                onClick = {
                                    selectedExercise = exercise
                                    isAddSetVisible = true
                                }
                            ) {
                                Text("Add Set")
                            }
                        }
                    }
                    }
                }
            }


        }

        if (isAddSetVisible) {
            AddSetDialog(
                workoutViewModel = workoutViewModel,
                onDismiss = { isAddSetVisible = false },
                onAddSet = { newSet ->
                    selectedExercise?.sets = selectedExercise?.sets?.plus(newSet)!!
                    isAddSetVisible = false
                },
                modifier = Modifier.fillMaxHeight(0.5F)
            )
            saveWorkout = false
        } else {
            saveWorkout = true
        }

        if (!isAddSetVisible) {
            Spacer(modifier = Modifier.weight(1F)) // Push the save workout button to the bottom

            // Add Exercise button placed here
            Button(
                onClick = { isExerciseDialogVisible = true },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp) // Optional: Add padding to separate from the save button
            ) {
                Text("Add Exercise")
            }

            if (saveWorkout) {
                Button(
                    onClick = {
                        workoutViewModel.createNewWorkout(workout)
                        isWorkoutAdded = true
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp) // Optional: Add padding to separate from the bottom edge
                ) {
                    Text("Save Workout")
                }
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

    if (isWorkoutAdded) {
        isWorkoutAdded = false
        navNext()
    }
}

@Composable
fun WorkoutDetailsEditScreen(
    workoutViewModel: WorkoutViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    workout: Mworkout?,
    navUp: () -> Unit = {},
    navNext: () -> Unit = {},
    removeSet: (Mexercise, Mset) -> Unit
) {
    var isExerciseDialogVisible by remember { mutableStateOf(false) }
    var isAddSetVisible by remember { mutableStateOf(false) }
    var selectedExercise by remember { mutableStateOf<Mexercise?>(null) }
    var isWorkoutAdded by remember { mutableStateOf(false) }
    var saveWorkout by remember { mutableStateOf(true) }



    workout?.let{
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(1F),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isAddSetVisible) {
            Text(
                text = "Workout: ${workout.name}",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(text = "Date: ${workout.getFormattedDate()}")
            Text(text = "Muscle Group: ${workout.muscleGroup}")

            // List exercises associated with the workout
            LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                items(workout.exercises) { exercise ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.95f) // Adjust width as needed
                                .padding(8.dp, 8.dp) // Optional: Add spacing between exercises
                                .clip(MaterialTheme.shapes.medium) // Apply rounded corners
                                .background(MaterialTheme.colorScheme.surface), // Background color
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = exercise.name,
                                    modifier = Modifier.padding(5.dp).weight(1f)
                                )
                                // Edit button to re-show Add Set under this exercise
                                IconButton(
                                    onClick = {
                                        selectedExercise = exercise
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit, // Pencil icon
                                        contentDescription = "Edit Exercise"
                                    )
                                }
                            }

                            // Show Add Set button only under the selected exercise
                            if (selectedExercise == exercise) {
                                exercise.sets.forEach { set ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = set.getFormattedSet(),
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(onClick = { removeSet(exercise, set) }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete set"
                                            )
                                        }

                                    }
                                }
                                Button(
                                    onClick = {
                                        selectedExercise = exercise
                                        isAddSetVisible = true
                                    }
                                ) {
                                    Text("Add Set")
                                }
                            }
                        }
                    }
                }
            }


        }

        if (isAddSetVisible) {
            AddSetDialog(
                workoutViewModel = workoutViewModel,
                onDismiss = { isAddSetVisible = false },
                onAddSet = { newSet ->
                    selectedExercise?.sets = selectedExercise?.sets?.plus(newSet)!!
                    isAddSetVisible = false
                },
                modifier = Modifier.fillMaxHeight(0.5F)
            )
            saveWorkout = false
        } else {
            saveWorkout = true
        }

        if (!isAddSetVisible) {
            Spacer(modifier = Modifier.weight(1F)) // Push the save workout button to the bottom

            // Add Exercise button placed here
            Button(
                onClick = { isExerciseDialogVisible = true },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp) // Optional: Add padding to separate from the save button
            ) {
                Text("Add Exercise")
            }

            if (saveWorkout) {
                Button(
                    onClick = {
                        workoutViewModel.editWorkout(workout)
                        isWorkoutAdded = true
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp) // Optional: Add padding to separate from the bottom edge
                ) {
                    Text("Save Workout")
                }
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

    if (isWorkoutAdded) {
        isWorkoutAdded = false
        navNext()
    }
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
    Column(modifier = modifier.fillMaxHeight(1F).fillMaxWidth(1F),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth(0.5F)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row {
                    IconButton(
                        onClick = {}
                    ) {
                        Image(
                            painter = painterResource(R.drawable.weight_plate),
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = {}
                    ) {
                        Image(
                            painter = painterResource(R.drawable.weight_plate),
                            contentDescription = null,
                        )
                    }
                    IconButton(
                        onClick = {}
                    ) {
                        Image(
                            painter = painterResource(R.drawable.weight_plate),
                            contentDescription = null
                        )
                    }
                }
                Row {
                    IconButton(
                        onClick = {}
                    ) {
                        Image(
                            painter = painterResource(R.drawable.weight_plate),
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = {}
                    ) {
                        Image(
                            painter = painterResource(R.drawable.weight_plate),
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = {}
                    ) {
                        Image(
                            painter = painterResource(R.drawable.weight_plate),
                            contentDescription = null
                        )
                    }
                }
            }
        }
        Column(modifier = Modifier.fillMaxHeight(1F)) {
            StatelessDataliftFormTextField(
                field = "Weight",
                text = weight.toString(),
                changeText = { weight = it.toDouble() },
                modifier = Modifier.fillMaxWidth(0.75f).align(Alignment.CenterHorizontally)
            )
            StatelessDataliftFormTextField(
                field = "Reps",
                text = reps.toString(),
                changeText = {
                    reps = if (it.isNotBlank()) {
                        it.toLong()
                    } else {
                        0
                    }
                },
                modifier = Modifier.fillMaxWidth(0.75f).align(Alignment.CenterHorizontally)
            )
            Button(
                onClick = { onAddSet(Mset(reps, weight)) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Confirm Set")
            }
        }
    }
}

@Preview
@Composable
fun WorkoutDetailsScreenPreview() {
    DataliftTheme {
        WorkoutDetailsScreen(
            workoutViewModel = viewModel(), // Provide the necessary ViewModel
            navUp = {},
            navNext = {}
        )
    }
}

@Preview
@Composable
fun AddSetDialogPreview() {
    DataliftTheme {
        AddSetDialog(
            onDismiss = {},
            onAddSet = {},
            workoutViewModel = viewModel(), // Provide the necessary ViewModel
            modifier = Modifier.fillMaxSize()
        )
    }
}
