package com.example.datalift.screens.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.datalift.model.Mworkout
import com.example.datalift.ui.theme.DataliftTheme
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.StateFlow

@Composable
fun WorkoutItem(
    workoutName: String,
    removeWorkout: () -> Unit,
){
    Row {
        Text(
            text = workoutName,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = { removeWorkout() }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Workout")
        }
    }
}

@Composable
fun WorkoutList(
    list: StateFlow<List<Mworkout>>,
    removeWorkout: (Mworkout) -> Unit,
    modifier: Modifier = Modifier
){
    val workoutList by list.collectAsState()
    LazyColumn(
        modifier = modifier
    ){
        items(items = workoutList) { workout ->
            WorkoutItem(
                workoutName = workout.name,
                removeWorkout = { removeWorkout(workout) }
            )
        }
    }
}


@Composable
fun WorkoutListScreen(
    workoutViewModel: WorkoutViewModel = viewModel(),
    modifier: Modifier = Modifier
){
    Box(modifier = modifier.padding(8.dp)){
        WorkoutList(
            list = workoutViewModel.workouts,
            removeWorkout = { workout -> workoutViewModel.remove(workout) },
            modifier = modifier.fillMaxSize()
        )
        IconButton (
            onClick = {
                workoutViewModel.add(
                    Mworkout("Workout",
                        date = Timestamp.now(),
                        "Back",
                        "temp",
                        emptyList())
                )
            },
            modifier = modifier
                .padding(12.dp)
                .clip(CircleShape)
                .align(Alignment.BottomEnd)
                .background(Color.Blue)
        ){
            Icon(Icons.Default.Add, contentDescription = "Add Workout")
        }
    }
}

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
}