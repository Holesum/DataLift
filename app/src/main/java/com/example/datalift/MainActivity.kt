package com.example.datalift

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.datalift.ui.DataliftApp
import com.example.datalift.ui.rememberDataliftAppState
import com.example.datalift.ui.theme.DataliftTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val appState = rememberDataliftAppState()

            DataliftTheme {
                DataliftApp(appState = appState)
            }
        }
    }
}