package com.example.datalift.screens.analysis

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.datalift.screens.workout.StatelessSearchExerciseDialog
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
    analysisViewModel.analyzeWorkouts()
    val uiState = analysisViewModel.uiState.collectAsStateWithLifecycle().value
    val exerciseUiState = analysisViewModel.searchExerciseUiState.collectAsStateWithLifecycle().value
    val exercise = analysisViewModel.exercise.collectAsState().value
    val apiResponse = analysisViewModel.apiResponseName.collectAsState().value
    val tempFlag = analysisViewModel.tempFlag.collectAsState().value

    AnalysisScreen(
        uiState = uiState,
        exerciseUiState = exerciseUiState,
        exercise = exercise,
        apiResponse = apiResponse,
        fetchExternalData = analysisViewModel::fetchExternalData,
        updateQuery = analysisViewModel::updateQuery,
        updateDisplays = analysisViewModel::updateDisplays,
        setExercise = analysisViewModel::setExercise,
        tempFlag = tempFlag,
        modifier = Modifier
    )
}



@Composable
internal fun AnalysisScreen(
    uiState: AnalysisUiState,
    exerciseUiState: SearchExerciseUiState,
    exercise: String = "",
    apiResponse: String = "",
    fetchExternalData: () -> Unit,
    setExercise: (String) -> Unit,
    updateQuery: (String) -> Unit,
    updateDisplays: (Boolean, Boolean?) -> Unit,
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

        if(exerciseUiState.recommendationDisplayed){
            fetchExternalData()
            item {
                Text(
                    text = "The exercise you are getting recommendations for is: ${exercise}",
                    modifier = Modifier.padding(top = 20.dp)
                )
            }
            item {
                Text(
                    text = "You're recommended workout is: ${apiResponse}"
                )
            }
        }

        item{
            Button(
                onClick = { updateDisplays(true, exerciseUiState.recommendationDisplayed) },
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text(text = "Recommendation")
            }
        }
    }

    StatelessSearchExerciseDialog(
        query = exerciseUiState.query,
        changeQuery = updateQuery,
        isVisible = exerciseUiState.dialogDisplayed,
        onDismiss = {
            updateDisplays(false,false)
        },
        onSelectExercise = { exercise ->
            setExercise(exercise.name)
            updateDisplays(false,true)
        }
    )


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
                exerciseUiState = SearchExerciseUiState(),
                fetchExternalData = {},
                updateQuery = { _ ->  },
                setExercise = { _ ->  },
                updateDisplays = { _,_ ->  },
                tempFlag = true,
            )
        }
    }
}