package com.example.datalift.screens.workout

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.datalift.model.ExerciseItem
import com.example.datalift.model.Mexercise
import com.example.datalift.model.Mset
import com.example.datalift.model.Mworkout
import com.example.datalift.navigation.Screens
import com.example.datalift.ui.components.StatelessDataliftFormTextField
import com.example.datalift.ui.theme.DataliftTheme
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.StateFlow


@Composable
fun SearchExerciseDialog(
    onDismiss: () -> Unit,
    onSelectExercise: (Mexercise) -> Unit,
    workoutViewModel: WorkoutViewModel = viewModel(),
) {
    var query by remember { mutableStateOf("") }
    val exercises = workoutViewModel.exercises.collectAsState().value
    // Search query changes trigger fetching exercises
    LaunchedEffect(query) {
        val handler = Handler(Looper.getMainLooper())
        var runnable: Runnable? = null

        runnable?.let { handler.removeCallbacks(it) }

        runnable = Runnable {
            // Trigger the exercise search after the delay
            workoutViewModel.getExercises(query)
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
                    onValueChange = { query = it;
                                    },
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



@Composable
fun WorkoutDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
    workoutViewModel: WorkoutViewModel = viewModel(),
) {
    var workoutName by remember { mutableStateOf("") }
    var selectedMuscleGroup by remember { mutableStateOf("") }
    var workout = workoutViewModel.workout.collectAsState().value

    val muscleGroups = listOf("Push", "Pull", "Legs", "Chest", "Arms", "Core", "Full Body")

    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Add Workout") },
            text = {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Workout name input
                    OutlinedTextField(
                        value = workoutName,
                        onValueChange = { workoutName = it },
                        label = { Text("Workout Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Muscle group dropdown
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedTextField(
                            value = selectedMuscleGroup,
                            onValueChange = { selectedMuscleGroup = it },
                            label = { Text("Muscle Group") },
                            readOnly = true, // Make it read-only since it's a dropdown
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { expanded = !expanded }) {
                                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            muscleGroups.forEach { muscleGroup ->
                                DropdownMenuItem(
                                    text = { Text(muscleGroup) },
                                    onClick = {
                                        selectedMuscleGroup = muscleGroup
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSave(workoutName, selectedMuscleGroup)
                        onDismiss()  // Close the dialog after saving

                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
fun WorkoutItem(
    workout: Mworkout,
    removeWorkout: () -> Unit,
){
    Row {
        Text(
            text = workout.name,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = { removeWorkout() }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Workout")
        }
    }
    Column(modifier = Modifier.border(1.dp, Color.Black), horizontalAlignment = Alignment.CenterHorizontally) {
        workout.exercises.forEach { exercise ->
            Text(text = exercise.getFormattedName())
        }
    }
}

@Composable
fun WorkoutList(
    list: List<Mworkout>,
    removeWorkout: (Mworkout) -> Unit,
    modifier: Modifier = Modifier
){
    LazyColumn(
        modifier = modifier
    ){
        items(items = list) { workout ->
            WorkoutItem(
                workout = workout,
                removeWorkout = { removeWorkout(workout) }
            )
            Spacer(modifier = Modifier.padding(20.dp))
        }
    }
}


@Composable
fun WorkoutListScreen(
    modifier: Modifier = Modifier,
    workoutViewModel: WorkoutViewModel = viewModel(),
    navNext: () -> Unit = {},
    navUp: () -> Unit = {}

){
    workoutViewModel.getWorkouts()

    var isDialogVisible by remember { mutableStateOf(false) }
    Box(modifier = modifier.padding(8.dp)){
        WorkoutList(
            list = workoutViewModel.workouts.collectAsState().value,
            removeWorkout = { workout -> workoutViewModel.deleteWorkout(workout) },
            modifier = modifier.fillMaxSize()
        )
        IconButton(
            onClick = { isDialogVisible = true },
            modifier = modifier
                .padding(12.dp)
                .clip(CircleShape)
                .align(Alignment.BottomEnd)
                .background(Color.Blue)
        ) {

            Icon(Icons.Default.Add, contentDescription = "Add Workout", tint = Color.Black)

        }
        WorkoutDialog(
            isVisible = isDialogVisible,
            onDismiss = { isDialogVisible = false },
            onSave = { workoutName, muscleGroup ->
                // Handle saving the workout here
                val newWorkout = Mworkout(
                    name = workoutName,
                    date = Timestamp.now(),
                    muscleGroup = muscleGroup, // Use the muscle group selected in the dialog
                    exercises = emptyList()  // This can be updated to have actual exercises
                )
                workoutViewModel.add(newWorkout) //adding to display list
                workoutViewModel.passWorkout(newWorkout)
                isDialogVisible = false // Close the dialog after saving
                navNext() // Navigate to the workout details screen
                Log.d("Firebase", "navigating to workout details")
            }
        )
    }
}

/*
@Preview(showBackground = true)
@Composable
fun WorkoutPreview(){
    DataliftTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ){ innerPadding ->
            WorkoutListScreen(
                modifier = Modifier.padding(innerPadding)
            )
        }

    }
}*/
