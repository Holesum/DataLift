package com.example.datalift.screens.analysis


import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.datalift.screens.workout.StatelessSearchExerciseDialog
import com.example.datalift.ui.components.SemiStatelessRadioOptionFieldToModal
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.auto
import com.patrykandpatrick.vico.compose.cartesian.axis.fraction
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.axis.BaseAxis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import java.text.SimpleDateFormat
import java.util.Locale



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


//@VisibleForTesting
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
    val isImperial = true
    val workoutProgressionModelProducer = remember { CartesianChartModelProducer() }
    val exerciseProgressionModelProducer = remember { CartesianChartModelProducer() }

//
//    //this is seems to be all of the data for the top chart
//    LaunchedEffect(uiState) {
//
//        if(uiState is AnalysisUiState.Success){
//            //I want to take the exerciseName field of each item in the list and put it into a list to let users choose from an exercise to view the progression of
//            Log.d("testing", uiState.exerciseAnalysis.toString())
//            //then I want to take the date of the progression and have that be the x value of the chart
//            //then for each exercise there is an initialAvgORM and for each progression there is a progression multiplier that I can multiple the initialAvgORM by to get the weight value
//            //I also will need to verify the metric or imperial with userRepo.getUnitSystem() tied to a variable called isImperial
//            val progressionData: List<Double> = uiState.workoutProgression.map {
//                it.totalProgression
//            }
//            val exerciseProgression: List<Double> = uiState.exerciseAnalysis
//                .filter { it.bodyPart == chosenBodyPart }
//                .map { it.initialAvgORM }
//
//            if(progressionData.isNotEmpty()){
//                workoutProgressionModelProducer.runTransaction {
//                    lineSeries { series(progressionData) }
//                }
//            }
//
//            if(exerciseProgression.isNotEmpty()){
//                exerciseProgressionModelProducer.runTransaction {
//                    lineSeries { series(exerciseProgression) }
//                }
//            } else {
//                exerciseProgressionModelProducer.runTransaction {
//                    lineSeries { series(0,0,0,0) }
//                }
//            }
//        }
//    }
// State to store formatted dates and weight values
    var formattedDates by remember { mutableStateOf<List<String>>(emptyList()) }
    var weightValues by remember { mutableStateOf<List<Double>>(emptyList()) }
    var timeInMillis by remember { mutableStateOf<List<Double>>(emptyList()) }
    var exerciseMin by remember { mutableStateOf(0.0) }


    // Handle data when UI state is successfully loaded
    LaunchedEffect(uiState) {
        if (uiState is AnalysisUiState.Success) {
            // Extract the exercise names for user selection
            val exerciseNames = uiState.exerciseAnalysis.map { it.exerciseName }.distinct()

            // Dynamic value for selected exercise (replace with user selection logic)
            val exerciseName = "bench press"

            // Find the progression data for the selected exercise
            val analysis = uiState.exerciseAnalysis.find { it.exerciseName.equals(exerciseName, ignoreCase = true) }

            // Convert progression dates to Date objects
            val exerciseDates = analysis?.progression?.map { it.date.toDate() } ?: emptyList()

            // Convert timestamps to numeric values (e.g., milliseconds)
            timeInMillis = exerciseDates.map { it.time.toDouble() }

            // Create a SimpleDateFormat for formatting the dates
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            formattedDates = exerciseDates.map { dateFormat.format(it) }


            // Get progression multipliers for Y-axis
            val progressionMultipliers = analysis?.progression?.map { it.progressionMultiplier } ?: emptyList()
            val initialAvgORM = analysis?.initialAvgORM ?: 0.0

            // Calculate weight values for Y-axis based on user's unit system
            weightValues = if (isImperial) {
                progressionMultipliers.map { it * initialAvgORM }
            } else {
                progressionMultipliers.map { it * initialAvgORM * 0.453592 }
            }

            Log.d("test", "weight values ${weightValues.toString()}")
            if(weightValues.isNotEmpty()){exerciseMin = weightValues.first()}
            Log.d("test", "min ${exerciseMin.toString()}")
            // Update chart data for workout progression
            // Ensure the data isn't empty before updating the chart
            if (timeInMillis.isNotEmpty() && weightValues.isNotEmpty()) {
                workoutProgressionModelProducer.runTransaction {

                    lineSeries { series(timeInMillis, weightValues) }
                }
            } else {
                // Provide fallback data (0.0) if the data is empty
                workoutProgressionModelProducer.runTransaction {
                    lineSeries { series(0.0, 0.0) }
                }
            }
        }
    }

    // Custom ValueFormatter for Date
    val dateFormatter = remember {
        CartesianValueFormatter { context, value, verticalAxisPosition ->
            val index = value.toInt() // Convert to int for index
            if (index < formattedDates.size) {
                formattedDates[index]
            } else {
                ""
            }
        }
    }

    // Setup X-axis for time (dates)
    val xAxis = HorizontalAxis.rememberBottom(
        valueFormatter = dateFormatter,
        guideline = null
    )

    // Update the chart with the data for exercise progression
    LaunchedEffect(weightValues) {
        // Ensure weightValues isn't empty before updating the chart
        if (weightValues.isNotEmpty()) {
            exerciseProgressionModelProducer.runTransaction {
                lineSeries { series(weightValues) }
            }
        } else {
            // Provide fallback data (0.0) if weightValues is empty
            exerciseProgressionModelProducer.runTransaction {
                lineSeries { series(0.0, 0.0, 0.0, 0.0) }
            }
        }
    }

    // LazyColumn for displaying the UI
    LazyColumn {
        when (uiState) {
            AnalysisUiState.Loading -> item {
                Text("Loading...")
            }
            AnalysisUiState.Error -> Unit
            is AnalysisUiState.Success -> {
                item {
                    Text("Workout Analysis")
                }

                item {
                    // Display the workout progression chart
                    if(formattedDates.isNotEmpty()) {
                        Log.d("test", "formattedDates ${formattedDates.toString()}")
                        ComposeBasicLineChart(
                            modelProducer = workoutProgressionModelProducer,
                            formattedDates =  formattedDates,
                            modifier = modifier.fillMaxWidth(),
                            min = exerciseMin - 10
                        )
                    } else {
                        Text("No data available")
                    }
                }

                item {
                    Text("This graph displays how the user is progressing in their workouts over time")
                }

                item {
                    // Exercise body part selection
                    SemiStatelessRadioOptionFieldToModal(
                        field = "Body Part",
                        selectedOption = chosenBodyPart,
                        changeSelectedOption = updateChosenBodyPart,
                        options = listOf("Push", "Pull", "Legs", "Chest", "Arms", "Core", "Full Body"),
                        modifier = modifier.padding(4.dp).fillMaxWidth(0.75f)
                    )
                }

//                item {
//                    // Display the exercise progression chart
//                    ComposeBasicLineChart(
//                        modelProducer = exerciseProgressionModelProducer,
//                        modifier = modifier.fillMaxWidth()
//                    )
//                }
            }
        }

        item {
            Button(
                onClick = { updateDisplays(true, exerciseUiState.recommendationDisplayed) },
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text(text = "Recommendation")
            }
        }
    }

    // Search Exercise Dialog
    StatelessSearchExerciseDialog(
        query = exerciseUiState.query,
        changeQuery = updateQuery,
        isVisible = exerciseUiState.dialogDisplayed,
        onDismiss = { updateDisplays(false, false) },
        onSelectExercise = { selectedExercise ->
            setExercise(selectedExercise.name)
            updateDisplays(false, true)
        }
    )
}

@Composable
private fun ComposeBasicLineChart(modelProducer: CartesianChartModelProducer,formattedDates: List<String>, min: Double = 0.0, modifier: Modifier) {
    val dateFormatter = remember(formattedDates) {
        CartesianValueFormatter { _, value, _ ->
            val index = value.toInt().coerceIn(0, formattedDates.lastIndex)
            formattedDates[index]
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                rangeProvider = CartesianLayerRangeProvider.fixed(minY = min)
            ),
            startAxis = VerticalAxis.rememberStart(guideline = null),
            //bottomAxis = HorizontalAxis.rememberBottom(guideline = null)
            bottomAxis = HorizontalAxis.rememberBottom(valueFormatter = dateFormatter,
                guideline = null)
        ),
        modelProducer = modelProducer,
        modifier = modifier
    )
}