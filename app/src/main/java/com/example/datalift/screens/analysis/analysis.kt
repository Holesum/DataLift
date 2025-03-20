package com.example.datalift.screens.analysis

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.datalift.screens.workout.StatelessSearchExerciseDialog
import com.example.datalift.ui.components.SemiStatelessRadioOptionFieldToModal
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
fun AnalysisRoute(
    analysisViewModel: analysisViewModel = hiltViewModel()
){
    analysisViewModel.analyzeWorkouts()
    val uiState = analysisViewModel.uiState.collectAsStateWithLifecycle().value
    val exerciseUiState = analysisViewModel.searchExerciseUiState.collectAsStateWithLifecycle().value
    val exercise = analysisViewModel.exercise.collectAsStateWithLifecycle().value
    val apiResponse = analysisViewModel.apiResponseName.collectAsStateWithLifecycle().value
    val tempFlag = analysisViewModel.tempFlag.collectAsStateWithLifecycle().value
    val chosenBodyPart = analysisViewModel.chosenBodyPart.collectAsStateWithLifecycle().value

    AnalysisScreen(
        uiState = uiState,
        exerciseUiState = exerciseUiState,
        exercise = exercise,
        apiResponse = apiResponse,
        chosenBodyPart = chosenBodyPart,
        fetchExternalData = analysisViewModel::fetchExternalData,
        updateChosenBodyPart = analysisViewModel::updateBodyPart,
        updateQuery = analysisViewModel::updateQuery,
        updateDisplays = analysisViewModel::updateDisplays,
        setExercise = analysisViewModel::setExercise,
        tempFlag = tempFlag,
        modifier = Modifier
    )
}


@VisibleForTesting
@Composable
internal fun AnalysisScreen(
    uiState: AnalysisUiState,
    exerciseUiState: SearchExerciseUiState,
    exercise: String = "",
    apiResponse: String = "",
    chosenBodyPart: String = "",
    fetchExternalData: () -> Unit,
    setExercise: (String) -> Unit,
    updateChosenBodyPart: (String) -> Unit,
    updateQuery: (String) -> Unit,
    updateDisplays: (Boolean, Boolean?) -> Unit,
    tempFlag: Boolean,  // We're keeping tempFlag to study performance with large # of data
    modifier: Modifier = Modifier
) {
    val workoutProgressionModelProducer = remember { CartesianChartModelProducer() }
    val exerciseProgressionModelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(uiState) {
        if(uiState is AnalysisUiState.Success){
            val progressionData: List<Double> = uiState.workoutProgression.map {
                it.totalProgression
            }
            val exerciseProgression: List<Double> = uiState.exerciseAnalysis
                .filter { it.bodyPart == chosenBodyPart }
                .map { it.initialAvgORM }

            if(progressionData.isNotEmpty()){
                workoutProgressionModelProducer.runTransaction {
                    lineSeries { series(progressionData) }
                }
            }

            if(exerciseProgression.isNotEmpty()){
                exerciseProgressionModelProducer.runTransaction {
                    lineSeries { series(exerciseProgression) }
                }
            } else {
                exerciseProgressionModelProducer.runTransaction {
                    lineSeries { series(0,0,0,0) }
                }
            }
        }
    }

    LaunchedEffect(chosenBodyPart){
        if (uiState is AnalysisUiState.Success){
            val progression: List<Double> = uiState.exerciseAnalysis
                .filter { it.bodyPart.equals(chosenBodyPart) }
                .map { it.initialAvgORM }

            if(progression.isNotEmpty()){
                exerciseProgressionModelProducer.runTransaction {
                    lineSeries { series(progression) }
                }
            } else {
                exerciseProgressionModelProducer.runTransaction {
                    lineSeries { series(0,0,0,0) }
                }
            }
        }
    }

//    LaunchedEffect(Unit) {
//        modelProducer.runTransaction {
//            lineSeries { series(13, 8, 7, 12, 0, 1, 15, 14, 0, 11, 6, 12, 0, 11, 12, 11) }
//        }[

//    }


    LazyColumn {
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
                    ComposeBasicLineChart(
                        modelProducer = workoutProgressionModelProducer,
                        modifier = modifier.fillMaxWidth()
                    )
                }
                item {
                    Text("This graph displays how the user is progressing in their workouts over time")
                }

                item{
                    SemiStatelessRadioOptionFieldToModal(
                        field = "Body Part",
                        selectedOption = chosenBodyPart,
                        changeSelectedOption = updateChosenBodyPart,
                        options = listOf("Push", "Pull", "Legs", "Chest", "Arms", "Core", "Full Body"),
                        modifier = modifier.padding(4.dp)
                            .fillMaxWidth(0.75f)
                    )
                }
                item{
                    ComposeBasicLineChart(
                        modelProducer = exerciseProgressionModelProducer,
                        modifier = modifier.fillMaxWidth()
                    )
                }
//                item{
//                    RadioOptionFieldToModal(
//                        field = "Body Part",
//                        options = listOf("Push", "Pull", "Legs", "Chest", "Arms", "Core", "Full Body"),
//                        modifier = modifier.padding(4.dp)
//                            .fillMaxWidth(0.75f)
//                    )
//                }
            }
        }

        if(exerciseUiState.recommendationDisplayed){
            fetchExternalData()
            item {
                Text(
                    text = "The exercise you are getting recommendations for is: $exercise",
                    modifier = Modifier.padding(top = 20.dp)
                )
            }
            item {
                Text(
                    text = "You're recommended workout is: $apiResponse"
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
        onSelectExercise = { selectedExercise ->
            setExercise(selectedExercise.name)
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