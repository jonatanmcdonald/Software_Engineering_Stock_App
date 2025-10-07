package com.example.loginsignup.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.loginsignup.screens.SignUpScreen
import com.example.loginsignup.screens.TermsAndConditionsScreen
import com.example.loginsignup.screens.WatchListScreen

@Composable
fun Nav(){

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "SignUpScreen") {

        composable(route = "SignUpScreen") {
            SignUpScreen(navController)
        }

        composable(route = "TermsAndConditionsScreen") {
            TermsAndConditionsScreen(navController)
        }

        composable(route = "WatchListScreen")
        {
            WatchListScreen(navController)
        }

    }
}