package com.example.datalift.screens.workout

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.datalift.designsystem.theme.DataliftTheme
import com.example.datalift.model.Mexercise
import com.example.datalift.model.Mset
import com.example.datalift.model.Mworkout
import com.example.datalift.ui.DevicePreviews
import com.example.datalift.ui.components.StatelessDataliftFormTextField
import com.example.datalift.ui.components.StatelessDataliftNumberTextField
import com.example.datalift.ui.components.StatelessDataliftTwoButtonDialog


@Composable
fun WorkoutDetailsScreen(
    modifier: Modifier = Modifier,
    workoutViewModel: WorkoutViewModel = hiltViewModel(),
    navUp: () -> Unit = {},
    navNext: () -> Unit = {}
) {
    var workout = Mworkout()
    var isAddPostVisible by remember { mutableStateOf(false) }
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
        Row(modifier = modifier.fillMaxWidth()) {
            IconButton(onClick = navUp) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
            Text(
                text = "Workouts",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterVertically)
                    .padding(start = 16.dp)
            )
        }
        HorizontalDivider(thickness = 2.dp)
        if (!isAddSetVisible && !isAddPostVisible) {
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
        if (isAddPostVisible) {
            AddPost(
                workoutViewModel = workoutViewModel,
                modifier = Modifier.fillMaxHeight(0.5F),
                onConfirmPost = {workoutViewModel.createNewWorkout(workout)
                    isWorkoutAdded = true}
            )
            saveWorkout = false
        } else {
            saveWorkout = true
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

        if (!isAddSetVisible && !isAddPostVisible) {
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
                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    Button(
                        onClick = {
                            workoutViewModel.createNewWorkout(workout)
                            isWorkoutAdded = true
                        },
                        modifier = Modifier
                            .padding(bottom = 16.dp) // Optional: Add padding to separate from the bottom edge
                    ) {
                        Text("Save Workout")
                    }
                    Button(
                        onClick = {
                            workoutViewModel.addPost = true
                            isAddPostVisible = true
                        }) {
                        Text("Add Post")
                    }
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
    modifier: Modifier = Modifier,
    workoutViewModel: WorkoutViewModel = hiltViewModel(),
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
        Row(modifier = modifier.fillMaxWidth()) {
            IconButton(onClick = navUp) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
            Text(
                text = "Workouts",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterVertically)
                    .padding(start = 16.dp)
            )
        }
        HorizontalDivider(thickness = 2.dp)
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
fun CreateSetDialog(
    height: Dp = 375.dp,
    padding: Dp = 16.dp,
    roundedCorners: Dp = 16.dp,
    isVisible: Boolean,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    StatelessDataliftTwoButtonDialog(
        height = height,
        padding = padding,
        roundedCorners = roundedCorners,
        isVisible = isVisible,
        buttonOneText = "Cancel",
        buttonTwoText = "Save",
        onDismissRequest = onDismiss,
        buttonOneAction = onDismiss,
        buttonTwoAction = onSave
    ){
        content()
    }
}


@Composable
fun AddSetDialog(
    onDismiss: () -> Unit,
    onAddSet: (Mset) -> Unit,
    workoutViewModel: WorkoutViewModel,
    modifier: Modifier = Modifier,
){
    Column(modifier = modifier.fillMaxHeight(1F).fillMaxWidth(1F),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Column(modifier = Modifier.fillMaxHeight(1F)) {
            StatelessDataliftNumberTextField(
                field = "Weight (lb)",
                suffix = "lbs",
                text = workoutViewModel.weight,
                changeText =  workoutViewModel.updateWeight ,
                isError = workoutViewModel.weightInvalid,
                supportingText = {
                    if(workoutViewModel.weightInvalid) {
                        Text("Weight needs to be an un-empty field")
                    }
                },
                modifier = Modifier.fillMaxWidth(0.75f).align(Alignment.CenterHorizontally)
            )

            StatelessDataliftNumberTextField(
                field = "Reps",
                suffix = "",
                text = workoutViewModel.reps,
                changeText =  workoutViewModel.updateReps ,
                isError = workoutViewModel.repsInvalid,
                supportingText = {
                    if(workoutViewModel.repsInvalid) {
                        Text("Reps needs to be an un-empty field")
                    }
                },
                modifier = Modifier.fillMaxWidth(0.75f).align(Alignment.CenterHorizontally)
            )
            Button(
                onClick = { onAddSet(Mset(workoutViewModel.reps.toLong(), workoutViewModel.weight.toDouble()))
                          workoutViewModel.weight = ""; workoutViewModel.reps = ""},
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Confirm Set")
            }
        }
    }
}

@Composable
fun AddPost(
    workoutViewModel: WorkoutViewModel,
    modifier: Modifier = Modifier,
    onConfirmPost: () -> Unit
){
    Column(modifier = modifier.fillMaxHeight(1F).fillMaxWidth(1F),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Column(modifier = Modifier.fillMaxHeight(1F)) {
            StatelessDataliftFormTextField(
                field = "Title",
                text = workoutViewModel.title,
                changeText =  workoutViewModel.updateTitle,
                modifier = Modifier.fillMaxWidth(0.75f).align(Alignment.CenterHorizontally)

            )
            StatelessDataliftFormTextField(
                field = "Body",
                text = workoutViewModel.body,
                changeText =  workoutViewModel.updateBody,
                modifier = Modifier.fillMaxWidth(0.75f).fillMaxHeight(.6f).align(Alignment.CenterHorizontally)
            )
            Button(onClick = onConfirmPost) {
                Text("Add Post and Save Workout")
            }
        }
    }
}

@DevicePreviews
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
