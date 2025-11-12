package com.example.loginsignup.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.loginsignup.components.AppBottomBar
import com.example.loginsignup.data.session.SessionManager
import com.example.loginsignup.navigation.MainDest.details
import com.example.loginsignup.screens.DetailsScreen
import com.example.loginsignup.screens.LogInScreen
import com.example.loginsignup.screens.NewsScreen
import com.example.loginsignup.screens.PortfolioScreen
import com.example.loginsignup.screens.SearchScreen
import com.example.loginsignup.screens.SignUpScreen
import com.example.loginsignup.screens.TermsAndConditionsScreen
import com.example.loginsignup.screens.TransactionScreen
import com.example.loginsignup.screens.WatchListScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    val isSignedIn = SessionManager.isUserSignedIn()

    // observe current route for showing the bottom bar
    val backEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backEntry?.destination?.route
    val tabRoutes = listOf(MainDest.HOME, MainDest.WATCHLIST, MainDest.TRANSACTION, MainDest.NEWS)
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
                val userId = SessionManager.getUserId() ?: return@composable
                PortfolioScreen(
                    userId = userId,
                    onNavigateToSearch = { navController.navigate(MainDest.SEARCH) }
                )
            }


            composable(MainDest.SEARCH) {
                val userId = SessionManager.getUserId() ?: return@composable
                SearchScreen(onBack = { navController.popBackStack()},
                    onNavigateToDetails = {ticker: String -> navController.navigate(details(
                        ticker,
                        userId))})}
            composable(
                route = MainDest.DETAILS,
                arguments = listOf(
                    navArgument("ticker") { type = NavType.StringType },
                    navArgument("userId"){type = NavType.IntType}
                )
            ) { backStackEntry ->
                val args = requireNotNull(backStackEntry.arguments)
                val ticker = requireNotNull(args.getString("ticker"))
                val userId = requireNotNull(args.getInt("userId"))
                DetailsScreen(ticker = ticker, userId = userId, onBack = { navController.navigate(MainDest.HOME) })
            }
            composable(MainDest.WATCHLIST) {
                val userId = SessionManager.getUserId() ?: return@composable
                WatchListScreen(userId)}

            composable(MainDest.TRANSACTION) {
                val userId = SessionManager.getUserId() ?: return@composable
                TransactionScreen(userId = userId) }

            composable(MainDest.NEWS)
            {
                NewsScreen()
            }
        }
    }
}

