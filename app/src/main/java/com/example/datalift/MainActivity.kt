package com.example.datalift

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.datalift.navigation.AppNavigation
import com.example.datalift.screens.logIn.LoginScreen
import com.example.datalift.screens.workout.WorkoutListScreen
import com.example.datalift.ui.theme.DataliftTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DataliftApp()
        }
    }
}

@Composable
fun DataliftApp(){
    DataliftTheme {
        AppNavigation()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DataliftApp()
}