package com.example.datalift.screens.analysis


import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.datalift.model.userWeights
import com.example.datalift.screens.workout.StatelessSearchExerciseDialog
import com.example.datalift.ui.components.SemiStatelessRadioOptionFieldToModal
import com.example.datalift.ui.components.StatelessDataliftNumberTextField
import com.google.firebase.Timestamp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.Insets
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
    val exerciseName = analysisViewModel.chartExercise.collectAsStateWithLifecycle().value
    val isImperial = analysisViewModel.getUnitSystem()
    val userWeight = analysisViewModel.userWeight.collectAsStateWithLifecycle().value
    val userWeights = analysisViewModel.userWeights.collectAsStateWithLifecycle().value

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
        exerciseName = exerciseName,
        updateExercise = analysisViewModel::updateExercise,
        isImperial = isImperial,
        userWeight = userWeight,
        changeUserWeight = analysisViewModel::setUserWeight,
        saveWeight = analysisViewModel::logUserWeight,
        userWeights = userWeights,
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
    exerciseName: String,
    updateExercise: (String) -> Unit,
    isImperial: Boolean,
    userWeight: String,
    changeUserWeight: (String) -> Unit,
    saveWeight: () -> Unit,
    userWeights: List<userWeights>,
    modifier: Modifier = Modifier
) {
    val workoutProgressionModelProducer = remember { CartesianChartModelProducer() }
    val exerciseProgressionModelProducer = remember { CartesianChartModelProducer() }
    val weightModelProducer = remember { CartesianChartModelProducer() }

// State to store formatted dates and weight values
    var formattedDates by remember { mutableStateOf<List<String>>(emptyList()) }
    var weightValues by remember { mutableStateOf<List<Double>>(emptyList()) }
    var timeInMillis by remember { mutableStateOf<List<Double>>(emptyList()) }
    var exerciseMin by remember { mutableDoubleStateOf(0.0) }
    var weightMin by remember { mutableDoubleStateOf(0.0) }
    var dropDown by remember {mutableStateOf(false)}
    var exerciseNames by remember {mutableStateOf<List<String>>(emptyList())}
    var expandWeight by remember {mutableStateOf(false)}
    var userWeightValues by remember {mutableStateOf<List<Double>>(emptyList())}
    var userWeightDates by remember {mutableStateOf<List<Timestamp>>(emptyList())}
    var formattedWeightDates by remember { mutableStateOf<List<String>>(emptyList()) }
    var reloadUI by remember {mutableStateOf(false)}



    // Handle data when UI state is successfully loaded
    LaunchedEffect(uiState,exerciseName, reloadUI, userWeights) {
        if (uiState is AnalysisUiState.Success) {
            reloadUI = false
            // Extract the exercise names for user selection
            exerciseNames = uiState.exerciseAnalysis.map { it.exerciseName }.distinct()
            Log.d("Chart", exerciseNames.toString())
            //updateExercise(exerciseNames.first())

            // Find the progression data for the selected exercise
            val analysis = uiState.exerciseAnalysis.find { it.exerciseName.equals(exerciseName, ignoreCase = true) }

            // Convert progression dates to Date objects
            val exerciseDates = analysis?.progression?.map { it.date.toDate() } ?: emptyList()

            // Convert timestamps to numeric values (e.g., milliseconds)
            timeInMillis = exerciseDates.map { it.time.toDouble() }

            // Create a SimpleDateFormat for formatting the dates
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            formattedDates = exerciseDates.map { dateFormat.format(it) }
            Log.w("test", "formattedDates ${formattedDates.toString()}")


            // Get progression multipliers for Y-axis
            val progressionMultipliers = analysis?.progression?.map { it.progressionMultiplier } ?: emptyList()
            val initialAvgORM = analysis?.initialAvgORM ?: 0.0

            // Calculate weight values for Y-axis based on user's unit system
            Log.d("test", "imperial: $isImperial")
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

                    lineSeries { series(weightValues) }
                }
            } else {
                // Provide fallback data (0.0) if the data is empty
                workoutProgressionModelProducer.runTransaction {
                    lineSeries { series(0.0, 0.0) }
                }
            }
        }
        //End of first chart data manipulation

        //Step one take list of user weights

        userWeightValues = userWeights.map { it.weight }
        userWeightDates = userWeights.map { it.date }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        formattedWeightDates = userWeightDates.map { dateFormat.format(it.toDate()) }

        if(userWeightValues.isNotEmpty()){
            if(!isImperial) {
                userWeightValues = userWeightValues.map { it * 0.453592 }
            }
            weightMin = userWeightValues.minOrNull()!!
            weightModelProducer.runTransaction {
                lineSeries { series(userWeightValues) }
            }
        }
    }

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
    LazyColumn(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
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
                SemiStatelessRadioOptionFieldToModal(
                    field = "Exercise",
                    selectedOption = exerciseName,
                    changeSelectedOption = updateExercise,
                    options = exerciseNames,
                    modifier = modifier.padding(4.dp).fillMaxWidth(0.75f)
                )
            }



                item {
                    // Exercise Progression Chart
                    if(formattedDates.isNotEmpty() && exerciseName != "") {
                        Log.d("test", "formattedDates ${formattedDates.toString()}")
                        Log.d("Chart", "weightValues: $weightValues")
                        val min : Double = if(isImperial) {
                            weightValues.minOrNull()!! - 10
                        } else {
                            weightValues.minOrNull()!! - 5
                        }
                        ComposeBasicLineChart(
                            modelProducer = workoutProgressionModelProducer,
                            formattedDates =  formattedDates,
                            modifier = modifier.fillMaxWidth(),
                            min = min,
                            title = exerciseName
                        )
                    } else {
                        Text("No data available")
                    }
                }
                //User Weight Chart, null check allows for non null assertion
                item {
                    if(formattedWeightDates.isNotEmpty()) {
                        val min : Double = if(isImperial) {
                            userWeightValues.minOrNull()!! - 10
                        } else {
                            userWeightValues.minOrNull()!! - 5
                        }
                        ComposeBasicLineChart(
                            modelProducer = weightModelProducer,
                            formattedDates = formattedWeightDates,
                            modifier = modifier.fillMaxWidth(),
                            min = min,
                            title = "User Weights"
                        )
                    }
                }

                item {
                    if(expandWeight) {
                        StatelessDataliftNumberTextField(
                            field = "User Weight",
                            text = userWeight,
                            changeText = changeUserWeight
                        )
                        Button(onClick = {
                            saveWeight()
                            expandWeight = false
                            reloadUI = true
                        }) {
                            Text(text = "Save Weight")
                        }
                    } else {
                        Button(onClick = { expandWeight = true }) {(Text(text = "Log New Weight"))}
                    }
                }
            }
        }

        if(exerciseUiState.recommendationDisplayed){
            item {
                Text(
                    text = "The exercise you are getting recommendations for is: $exercise",
                    modifier = Modifier.padding(top = 20.dp)
                )
            }
            item {
                Text(
                    text = "Your recommended workout is: $apiResponse"
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
private fun ComposeBasicLineChart(modelProducer: CartesianChartModelProducer,formattedDates: List<String>, min: Double = 0.0, title: String = "title check", modifier: Modifier) {
    val dateFormatter = remember(formattedDates) {
        CartesianValueFormatter { _, value, _ ->
            val index = value.toInt().coerceIn(0, formattedDates.lastIndex)
            formattedDates[index]
        }
    }
    Log.d("Chart", "min: $min")

    val labelComponent = rememberTextComponent(
        margins = Insets(4F),
        padding = Insets(8F, 2F, 8F, 4F),
        background = rememberShapeComponent()
    )

Column {

    Text(
        text = title,
        modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally)
    )

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                rangeProvider = CartesianLayerRangeProvider.fixed(minY = min)
            ),
            startAxis = VerticalAxis.rememberStart(guideline = null),
            //bottomAxis = HorizontalAxis.rememberBottom(guideline = null)
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = dateFormatter,
                guideline = null
            ),
            marker = rememberDefaultCartesianMarker(
                label = labelComponent
            ),
        ),
        modelProducer = modelProducer,

        modifier = modifier
    )
}
}



