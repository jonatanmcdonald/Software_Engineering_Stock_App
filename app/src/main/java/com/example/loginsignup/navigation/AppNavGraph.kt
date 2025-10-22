package com.example.loginsignup.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.loginsignup.components.AppBottomBar
import com.example.loginsignup.screens.HomeScreen
import com.example.loginsignup.screens.LogInScreen
import com.example.loginsignup.screens.SignUpScreen
import com.example.loginsignup.screens.TermsAndConditionsScreen
import com.example.loginsignup.screens.WatchListScreen

@Composable
fun AppNavHost(isSignedIn: Boolean) {
    val navController = rememberNavController()

    // observe current route for showing the bottom bar
    val backEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backEntry?.destination?.route
    val tabRoutes = listOf(MainDest.HOME, MainDest.WATCHLIST, MainDest.PROFILE)
    val showBottomBar = currentRoute in tabRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(
                    tabs = tabRoutes,
                    currentRoute = currentRoute,
                    onTabSelected = { route ->
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            // pick start based on auth
            startDestination = if (isSignedIn) MainDest.HOME else AuthDest.SIGN_UP,
            modifier = Modifier.padding(padding)
        ) {
            // ---- AUTH screens (no bottom bar since routes not in tabRoutes) ----
            composable(AuthDest.SIGN_UP) {
                SignUpScreen(
                    onViewTerms = { navController.navigate(AuthDest.TERMS) },

                    onViewSignIn = {
                        navController.navigate(AuthDest.LOGIN)
                    }

                )
            }
            composable(AuthDest.LOGIN) {
                LogInScreen(
                    onSignedIn = {
                        navController.navigate(MainDest.HOME) {
                            popUpTo(AuthDest.LOGIN) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onViewTerms = {navController.navigate(AuthDest.TERMS)},
                    userViewModel = viewModel()
                )
            }

            composable(AuthDest.TERMS) {
                TermsAndConditionsScreen(onBack = { navController.popBackStack() })
            }

            // ---- MAIN tabs ----
            composable(MainDest.HOME) {
                HomeScreen(
                    //onOpenDetails = { id -> navController.navigate("home/details/$id") }
                )
            }
            composable(MainDest.WATCHLIST) { WatchListScreen("0") }
            composable(MainDest.PROFILE) { ProfileScreen() }

            // ---- deeper routes (bottom bar auto-hidden) ----
            /*composable("home/details/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                HomeDetailsScreen(id, onBack = { navController.popBackStack() })
            }*/
        }
    }
}

@Composable
fun ProfileScreen() {
    TODO("Not yet implemented")
}
