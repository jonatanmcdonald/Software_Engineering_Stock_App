package com.example.loginsignup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginsignup.viewModels.AuthViewModel
import com.example.loginsignup.navigation.AppNavHost

class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent{
            val vm: AuthViewModel = viewModel()

            // 2) Observe sign-in state (lifecycle-aware)
            val isSignedIn by vm.isSignedIn.collectAsStateWithLifecycle()

            // 3) Pass it down
            AppNavHost(isSignedIn = true)
        }
    }
}
