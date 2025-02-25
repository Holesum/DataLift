package com.example.datalift.screens.analysis

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.datalift.model.Mexercise
import com.example.datalift.screens.workout.WorkoutViewModel
import com.example.datalift.ui.theme.DataliftTheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment


// Displays two different charts
// Displays Workout Progression
    // x-axis time, y-axis = Progression Value

// Displays Exercise Progression
    // x-axis time, y-axis = Progression Value

// Workout Progression averages progression
//
@Composable
fun AnalysisScreen(
    analysisViewModel: analysisViewModel = viewModel()
){
    val uiState = analysisViewModel.uiState.collectAsStateWithLifecycle().value
    val tempFlag = analysisViewModel.tempFlag.collectAsState().value
    val search = remember { mutableStateOf(false) }
    val searched = remember { mutableStateOf(false) }

    Column {
        AnalysisScreen(
            uiState = uiState,
            tempFlag = tempFlag,
            modifier = Modifier
        )

        Spacer(modifier = Modifier.weight(0.5F))

        if (search.value) {
            com.example.datalift.screens.workout.SearchExerciseDialog(
                onDismiss = { search.value = false; searched.value = true },
                onSelectExercise = { exercise ->
                    analysisViewModel.setExercise(exercise.name)
                    search.value = false
                    searched.value = true
                }
            )
        }

        if(searched.value){
            analysisViewModel.fetchExternalData()
            Column {
                Text(text = "The exercise you are getting recommendations for is: ${analysisViewModel.exercise.collectAsState().value}")
                Text(text = "You're recommended workout is: ${analysisViewModel.apiResponseName.collectAsState().value}")
            }
        }

        Spacer(modifier = Modifier.weight(0.5F))


        Button( onClick = {search.value = true}, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(text = "Recommenation")
        }
    }
}

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
internal fun AnalysisScreen(
    uiState: AnalysisUiState,
    tempFlag: Boolean,  // We're keeping tempFlag to study performance with large # of data
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(uiState) {
            if(uiState is AnalysisUiState.Success){
                val progressionData: List<Double> = uiState.workoutProgression.map { it.totalProgression }
                if(progressionData.isNotEmpty()){
                    modelProducer.runTransaction {
                        lineSeries { series(progressionData) }
                    }
                }
            }
    }

//    LaunchedEffect(Unit) {
//        modelProducer.runTransaction {
//            lineSeries { series(13, 8, 7, 12, 0, 1, 15, 14, 0, 11, 6, 12, 0, 11, 12, 11) }
//        }
//    }


    LazyColumn {
        item{
            Text(
                text = "Analysis Screen",
                fontSize = 28.sp
            )
        }
        when(uiState) {
            AnalysisUiState.Loading -> item {
                Text("Loading")
            }
            AnalysisUiState.Error -> TODO()
            is AnalysisUiState.Success -> {
                item{
                    Text("Workout Analysis")
                }
                item {
                    ComposeBasicLineChart(modelProducer, modifier = modifier.fillMaxWidth())
                }
                item {
                    Text("This graph displays how the user is progressing in their workouts over time")
                }
            }
        }
    }

}

@Composable
private fun ComposeBasicLineChart(modelProducer: CartesianChartModelProducer, modifier: Modifier) {
    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
    )
}

@Preview
@Composable
fun AnalysisScreenPreview(){
    DataliftTheme {
        Surface {
            AnalysisScreen(
                uiState = AnalysisUiState.Success(
                    workoutProgression = emptyList(),
                    exerciseAnalysis = emptyList()
                ),
                tempFlag = true,
            )
        }
    }
}