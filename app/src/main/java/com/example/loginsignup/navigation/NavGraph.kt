package com.example.loginsignup.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.loginsignup.screens.SignUpScreen
import com.example.loginsignup.screens.TermsAndConditionsScreen

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


    }
}