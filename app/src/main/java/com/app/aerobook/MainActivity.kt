package com.app.aerobook

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
import androidx.navigation.compose.rememberNavController
import com.app.aerobook.presentation.navigation.NavGraph
import com.app.aerobook.ui.theme.AeroBookTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // Critical: This tells Hilt to inject dependencies into this Activity
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enabling Edge-to-Edge makes the map look immersive under status bars
        enableEdgeToEdge()

        setContent {
            // Your custom theme wrapper
            AeroBookTheme {
                // 1. Create the NavController (The manager)
                val navController = rememberNavController()

                // 2. Call the NavGraph (The map of your screens)
                // We pass the controller so the Graph can use it to switch screens
                NavGraph(navController = navController)
            }
        }
    }
}