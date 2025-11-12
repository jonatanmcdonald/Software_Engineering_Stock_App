package com.example.loginsignup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.loginsignup.navigation.AppNavHost
import com.example.loginsignup.ui.theme.MyAppTheme

class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent {

            MyAppTheme {
                AppNavHost()
            }
        }
    }
}
