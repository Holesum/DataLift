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
import com.example.datalift.ui.theme.DataliftTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.example.datalift.screens.logIn.LogInViewModel

private lateinit var viewModel: LogInViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = LogInViewModel()
        enableEdgeToEdge()
        setContent {
            DataliftTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                    //function call to test create user
                    //viewModel.createDBUser("test@example.com", "test name", 70, 150, true, true, "password")
                    viewModel.loginUser("test@example.com", "password")


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