package com.example.florida

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.florida.model.SessionManager
import com.example.florida.ui.home.MainApp
import com.example.florida.ui.theme.FloridaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionManager.initialize(this)
        enableEdgeToEdge()
        setContent {
            FloridaTheme {
                MainApp(
                )
            }
        }
    }
}