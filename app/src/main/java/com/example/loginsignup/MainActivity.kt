package com.example.loginsignup

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.loginsignup.navigation.AppNavHost
import com.example.loginsignup.ui.theme.MyAppTheme

// This is the main activity of the application.
class MainActivity : ComponentActivity()
{
    // This requests notification permission from the user.
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            Log.d("MainActivity", "Notification permission granted? $granted")
        }

    override fun onCreate(savedInstanceState: Bundle?) // This function is called when the activity is created.
    {
        super.onCreate(savedInstanceState)
        ensureNotificationPermission() // Ensures that the app has notification permission.

        setContent { // Sets the content of the activity.

            MyAppTheme { // Applies the app theme.
                AppNavHost() // Sets the navigation host.
            }
        }
    }

    // This function ensures that the app has notification permission.
    private fun ensureNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Checks if the Android version is Tiramisu or higher.
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) // Checks if the app has notification permission.
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS) // Requests notification permission.
            }
        }
    }
}


