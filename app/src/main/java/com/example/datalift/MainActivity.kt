package com.example.datalift

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.datalift.model.Mexercise
import com.example.datalift.model.Mset
import com.example.datalift.model.Mworkout
import com.example.datalift.ui.theme.DataliftTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.example.datalift.screens.logIn.LogInViewModel
import com.example.datalift.screens.workout.WorkoutViewModel
import com.google.firebase.Timestamp

//to test login
//private lateinit var viewModel: LogInViewModel

//to test workout
private lateinit var viewModel: WorkoutViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //to test login
        //viewModel = LogInViewModel()
        //to test workout
        viewModel = WorkoutViewModel()
        enableEdgeToEdge()
        setContent {
            DataliftTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                    //function call to test create user
                    //viewModel.createDBUser("test@example.com", "test name", "Male", 70, 150, true, true, "password")
                    //viewModel.loginUser("test@example.com", "password")
                    val newWorkout = Mworkout(
                        name = "Leg Day",
                        date = Timestamp.now(),
                        muscleGroup = "Legs",
                        exercises = listOf(
                            Mexercise(
                                id = "exercise1",
                                name = "Squats",
                                sets = listOf(Mset(rep = 10, weight = 100.0), Mset(rep = 8, weight = 110.0))
                            ), Mexercise(
                                id = "exercise2",
                                name = "Leg Press",
                                sets = listOf(Mset(rep = 10, weight = 100.0), Mset(rep = 8, weight = 110.0))
                            )
                        )
                    )
                    viewModel.getWorkouts()

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name! I'm making changes!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DataliftTheme {
        Greeting("Android")
    }
}