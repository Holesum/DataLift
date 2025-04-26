package com.example.datalift.screens.workout

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.datalift.designsystem.DataliftTheme
import com.example.datalift.R
import com.example.datalift.model.Mexercise
import com.example.datalift.model.Mset
import com.example.datalift.model.Mworkout
import com.example.datalift.ui.DevicePreviews
import com.example.datalift.ui.components.SemiStatelessDataliftMenu
import com.example.datalift.ui.components.StatelessDataliftCloseCardDialog
import com.example.datalift.ui.components.StatelessDataliftFormTextField
import com.example.datalift.ui.components.StatelessDataliftTwoButtonDialog


@Composable
fun StatelessSearchExerciseDialog(
    query: String,
    changeQuery: (String) -> Unit,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSelectExercise: (Mexercise) -> Unit,
    workoutViewModel: WorkoutViewModel = hiltViewModel()
) {
    val exercises = workoutViewModel.exercises.collectAsState().value

    LaunchedEffect(query) {
        val handler = Handler(Looper.getMainLooper())
        var runnable: Runnable? = null

        runnable?.let { handler.removeCallbacks(it) }  // I don't think this runs ever

        runnable = Runnable {
            // Trigger the exercise search after the delay
            workoutViewModel.getExercises(query)
        }

        handler.postDelayed(runnable, 500) // Delay for 500ms before fetching exercises
    }

    StatelessDataliftCloseCardDialog(
        isVisible = isVisible,
        onDismissRequest = onDismiss,
    ) {
        Column(modifier = Modifier.fillMaxHeight(0.8f)) {
            OutlinedTextField(
                value = query,
                onValueChange = changeQuery,
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
    }
}

@Composable
fun SearchExerciseDialog(
    onDismiss: () -> Unit,
    onSelectExercise: (Mexercise) -> Unit,
    workoutViewModel: WorkoutViewModel = hiltViewModel(),
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


@Composable
fun WorkoutCreationDialog(
    height: Dp = 375.dp,
    padding: Dp = 16.dp,
    roundedCorners: Dp = 16.dp,
    isVisible: Boolean,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
){
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
fun WorkoutDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
    workoutViewModel: WorkoutViewModel = hiltViewModel(),
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
fun WorkoutItemCard(
    workout: Mworkout,
    onWorkoutClick: (String) -> Unit,
    onWorkoutEditClick: (String) -> Unit,
    removeWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onWorkoutClick(workout.docID) },
        modifier = modifier.padding(5.dp)
    ) {
        Column {
            Row {
                Text(
                    text = workout.name,
                    modifier = Modifier.weight(1f)
                        .padding(start = 8.dp)
                        .align(Alignment.CenterVertically)
                )
                IconButton(
                    onClick = { onWorkoutEditClick(workout.docID) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit, // Pencil icon
                        contentDescription = "Edit Exercise"
                    )
                }

                IconButton(onClick = { removeWorkout() }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Workout")
                }
            }
        }
    }
}

@Preview
@Composable
fun WorkoutItemCardPreview(){
    DataliftTheme {
        Surface {
            WorkoutItemCard(
                workout = Mworkout(
                    name = "Test",
                    exercises = testExerciseList()
                ),
                onWorkoutClick = {},
                onWorkoutEditClick = {},
                removeWorkout = {},
            )
        }
    }
}

@Composable
fun WorkoutList(
    list: List<Mworkout>,
    onWorkoutClick: (String) -> Unit,
    onWorkoutEditClick: (String) -> Unit,
    removeWorkout: (Mworkout) -> Unit,
    modifier: Modifier = Modifier
){
    LazyColumn(
        modifier = modifier
    ){
        items(items = list) { workout ->
            WorkoutItemCard(
                workout = workout,
                onWorkoutClick = onWorkoutClick,
                onWorkoutEditClick = onWorkoutEditClick,
                removeWorkout = { removeWorkout(workout) }
            )
        }
    }
}


@Composable
fun WorkoutListScreen(
    modifier: Modifier = Modifier,
    workoutViewModel: WorkoutViewModel = hiltViewModel(),
    onWorkoutClick: (String) -> Unit,
    onWorkoutEditClick: (String) -> Unit,
    navNext: () -> Unit = {},
//    navUp: () -> Unit = {}

){
    workoutViewModel.getWorkouts()
    val dialogUiState by workoutViewModel.dialogUiState.collectAsStateWithLifecycle()

    var isDialogVisible by remember { mutableStateOf(false) }

    Box(modifier = modifier.padding(8.dp)){
        WorkoutList(
            list = workoutViewModel.workouts.collectAsState().value,
            onWorkoutClick = onWorkoutClick,
            onWorkoutEditClick = onWorkoutEditClick,
            removeWorkout = { workout -> workoutViewModel.deleteWorkout(workout) },
            modifier = modifier.fillMaxSize()
        )
        IconButton(
            onClick = { isDialogVisible = true },
            modifier = modifier
                .padding(12.dp)
                .clip(CircleShape)
                .align(Alignment.BottomCenter)
                .size(64.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.weight_plate),
                contentDescription = null,
                modifier.size(64.dp)
            )
        }
        WorkoutCreationDialog(
            isVisible = isDialogVisible,
            onDismiss = { isDialogVisible = false},
            onSave = {
                if (workoutViewModel.validateDialog(
                        workoutName = dialogUiState.workoutName,
                        muscleGroup = dialogUiState.muscleGroup
                    )
                ){
                    val newWorkout = Mworkout(
                        name = dialogUiState.workoutName,
                        muscleGroup = dialogUiState.muscleGroup,
                    )
                    workoutViewModel.add(newWorkout)
                    workoutViewModel.passWorkout(newWorkout)
                    isDialogVisible = false
                    navNext() // Navigate to the workout details screen
                    Log.d("Firebase", "navigating to workout details")
                }
            }
        ) {
            Text(
                text = "Add Workout",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .padding(16.dp)
            )

            HorizontalDivider(
                modifier = Modifier.height(4.dp)
            )

            StatelessDataliftFormTextField(
                field = "Workout Name",
                text = dialogUiState.workoutName,
                changeText = workoutViewModel::updateDialogWorkoutName,
                isError = dialogUiState.workoutNameError,
                supportingText = {
                    if(dialogUiState.workoutNameError){
                        Text("Workout Name has to be non empty")
                    }
                },
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp)
            )

            SemiStatelessDataliftMenu(
                field = "Muscle Group",
                text = dialogUiState.muscleGroup,
                isError = dialogUiState.muscleGroupError,
                supportingText = {
                    if(dialogUiState.muscleGroupError){
                        Text("User must select one of the Muscle Groups")
                    }
                },
                selectOption = workoutViewModel::updateDialogWorkoutMuscleGroup,
                options = listOf("Push", "Pull", "Legs", "Chest", "Arms", "Core", "Full Body"),
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp)
            )
        }

//        WorkoutDialog(
//            isVisible = isDialogVisible,
//            onDismiss = { isDialogVisible = false },
//            onSave = { workoutName, muscleGroup ->
//                // Handle saving the workout here
//                val newWorkout = Mworkout(
//                    name = workoutName,
//                    date = Timestamp.now(),
//                    muscleGroup = muscleGroup, // Use the muscle group selected in the dialog
//                    exercises = emptyList()  // This can be updated to have actual exercises
//                )
//                workoutViewModel.add(newWorkout) //adding to display list
//                workoutViewModel.passWorkout(newWorkout)
//                isDialogVisible = false // Close the dialog after saving
//                navNext() // Navigate to the workout details screen
//                Log.d("Firebase", "navigating to workout details")
//            }
//        )
    }
}

@Composable
fun ExerciseCard(
    exercise: Mexercise,
    modifier: Modifier = Modifier
){
    var expanded by remember { mutableStateOf(false) }

    Card(
        onClick = { expanded = !expanded },
        modifier = modifier.padding(5.dp)
    ) {
        Column {
            Row {
                Text(
                    text = exercise.getFormattedName(),
                    modifier = Modifier.weight(1f)
                        .padding(start = 8.dp)
                        .align(Alignment.CenterVertically)
                )
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector =
                            if(expanded) { Icons.Default.KeyboardArrowDown } else Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Expand/Dismiss Workout"
                    )
                }
            }
            if(expanded){
                Column {
                    exercise.sets.forEach { set ->
                        Row {
                            Spacer(modifier = Modifier.padding(15.dp))
                            Text(text = set.getFormattedSet())
                        }
                    }
                    Spacer(modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}


@Composable
fun WorkoutScreen(
    workout: Mworkout?,
    navUp: () -> Unit,
    modifier: Modifier = Modifier
){
    workout?.let {
        Column(modifier = modifier) {

            Row {
                IconButton(onClick = navUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Navigate Back"
                    )
                }
                Text(
                    text = workout.name,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            HorizontalDivider(thickness = 2.dp)
            LazyColumn {
                items(workout.exercises) { exercise ->
                    ExerciseCard(exercise)
                }
            }
        }
    }

}


fun testExerciseList() : List<Mexercise> {
    return listOf(
        Mexercise(
            id = "1",
            name = "Lift McDonald's",
            sets = listOf(
                Mset(
                    rep = 5,
                    weight = 30.0
                ),
                Mset(
                    rep = 8,
                    weight = 30.0
                )
            )
        ),
        Mexercise(
            id = "2",
            name = "Lift Broke People",
            sets = listOf(
                Mset(
                    rep = 5,
                    weight = 110.0
                ),
            )
        ),
    )
}

@Preview
@Composable
fun StatelessSearchExerciseDialogPreview() {
    DataliftTheme {
        Surface {
            StatelessSearchExerciseDialog(
                query = "Push-up",
                changeQuery = {},
                isVisible = true,
                onDismiss = {},
                onSelectExercise = {}
            )
        }
    }
}

@Preview
@Composable
fun SearchExerciseDialogPreview() {
    DataliftTheme {
        Surface {
            SearchExerciseDialog(
                onDismiss = {},
                onSelectExercise = {}
            )
        }
    }
}

@Preview
@Composable
fun WorkoutDialogPreview() {
    DataliftTheme {
        Surface {
            WorkoutCreationDialog(
                isVisible = true,
                onDismiss = {},
                onSave = {}
            ){
                Text(
                    text = "Add Workout",
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                        .padding(
                            top = 16.dp,
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
                )
                HorizontalDivider(
                    modifier = Modifier.height(4.dp)
                )

                StatelessDataliftFormTextField(
                    field = "Workout Name",
                    text = "",
                    changeText = {},
                    supportingText = {

                    },
                    modifier = Modifier.fillMaxWidth()
                        .padding(8.dp)
                )

                SemiStatelessDataliftMenu(
                    field = "Muscle Group",
                    text = "",
                    selectOption = {},
                    supportingText = {

                    },
                    options = listOf("Push", "Pull", "Legs", "Chest", "Arms", "Core", "Full Body"),
                    modifier = Modifier.fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}


@Preview
@Composable
fun WorkoutListPreview() {
    DataliftTheme {
        Surface {
            WorkoutList(
                list = listOf(
                    Mworkout(
                        name = "Test Workout 1",
                        exercises = testExerciseList()
                    ),
                    Mworkout(
                        name = "Test Workout 2",
                        exercises = testExerciseList()
                    )
                ),
                onWorkoutClick = {},
                onWorkoutEditClick = {},
                removeWorkout = {}
            )
        }
    }
}

@DevicePreviews
@Composable
fun WorkoutScreenPreview() {
    DataliftTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            WorkoutScreen(
                workout = Mworkout(
                    name = "Test Workout",
                    exercises = testExerciseList()
                ),
                navUp = {}
            )
        }
    }
}

@Preview
@Composable
fun ExerciseCardPreview() {
    DataliftTheme {
        Surface {
            ExerciseCard(
                exercise = Mexercise(
                    id = "1",
                    name = "Lift McDonald's",
                    sets = listOf(
                        Mset(
                            rep = 5,
                            weight = 30.0
                        ),
                        Mset(
                            rep = 8,
                            weight = 30.0
                        )
                    )
                )
            )
        }
    }
}