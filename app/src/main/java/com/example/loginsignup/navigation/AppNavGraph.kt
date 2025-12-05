// Package declaration for the navigation
package com.example.loginsignup.navigation

// Import statements for necessary classes and libraries
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.example.loginsignup.screens.ForgotPasswordScreen
import com.example.loginsignup.screens.LogInScreen
import com.example.loginsignup.screens.NewsScreen
import com.example.loginsignup.screens.PortfolioScreen
import com.example.loginsignup.screens.SearchScreen
import com.example.loginsignup.screens.SignUpScreen
import com.example.loginsignup.screens.TermsAndConditionsScreen
import com.example.loginsignup.screens.TransactionScreen
import com.example.loginsignup.screens.WatchListScreen

/**
 * The main navigation host for the application.
 */
@Composable
fun AppNavHost() {
    // Create a NavController to handle navigation within the app.
    val navController = rememberNavController()

    // Check if the user is currently signed in using the SessionManager.
    val isSignedIn = SessionManager.isUserSignedIn()

    // Observe the current back stack entry to determine the current route.
    val backEntry by navController.currentBackStackEntryAsState()
    // Get the route string of the current destination.
    val currentRoute = backEntry?.destination?.route
    // Define the list of routes that should display the bottom navigation bar.
    val tabRoutes = listOf(MainDest.HOME, MainDest.WATCHLIST, MainDest.TRANSACTION, MainDest.NEWS)
    // Determine whether the bottom bar should be shown based on the current route.
    val showBottomBar = currentRoute in tabRoutes

    // Use a Scaffold to provide a standard layout structure, including the bottom bar.
    Scaffold(
        bottomBar = {
            // Show the bottom bar only if the current route is one of the main tab routes.
            if (showBottomBar) {
                AppBottomBar(
                    tabs = tabRoutes,
                    currentRoute = currentRoute,
                    onTabSelected = { route ->
                        // When a tab is selected, navigate to the corresponding route.
                        navController.navigate(route) {
                            // `launchSingleTop = true` avoids creating a new instance of a screen if it's already on top.
                            launchSingleTop = true
                            // `restoreState = true` restores the state of the destination when navigating to it.
                            restoreState = true
                            // Pop up to the start destination of the graph to avoid building up a large back stack.
                            popUpTo(navController.graph.findStartDestination().id) {
                                // `saveState = true` saves the state of the popped screens.
                                saveState = true
                            }
                        }
                    }
                )
            }
        }
    ) { padding ->
        // NavHost is the container for all navigation destinations.
        NavHost(
            navController = navController,
            // Set the start destination based on whether the user is signed in.
            startDestination = if (isSignedIn) MainDest.HOME else AuthDest.SIGN_UP,
            // Apply padding provided by the Scaffold.
            modifier = Modifier.padding(padding)
        ) {
            // ---- AUTH screens (no bottom bar since routes not in tabRoutes) ----
            // Define the composable for the Sign Up screen.
            composable(AuthDest.SIGN_UP) {
                SignUpScreen(
                    // Navigate to the Terms and Conditions screen when the user clicks the corresponding link.
                    onViewTerms = { navController.navigate(AuthDest.TERMS) },

                    // Navigate to the Login screen when the user clicks the "Sign In" button.
                    onViewSignIn = {
                        navController.navigate(AuthDest.LOGIN)
                    }

                )
            }
            // Define the composable for the Login screen.
            composable(AuthDest.LOGIN) {
                LogInScreen(
                    // After successful sign-in, navigate to the Home screen.
                    onSignedIn = {
                        // Navigate to the home screen.
                        navController.navigate(MainDest.HOME) {
                            // Pop the login screen off the back stack so the user can't navigate back to it.
                            popUpTo(AuthDest.LOGIN) { inclusive = true }
                            // Ensure there's only one instance of the Home screen.
                            launchSingleTop = true
                        }
                    },
                    // Navigate to the Terms and Conditions screen.
                    onForgotPassword = {navController.navigate(AuthDest.RESET)},

                )
            }

            // Define the composable for the Terms and Conditions screen.
            composable(AuthDest.TERMS) {
                // The `onBack` callback pops the back stack to return to the previous screen.
                TermsAndConditionsScreen(onBack = { navController.popBackStack() })
            }

            // ---- MAIN tabs ----
            // Define the composable for the Home screen (Portfolio).
            composable(MainDest.HOME) {
                // Get the user ID from the session; if it's null, do nothing.
                val userId = SessionManager.getUserId() ?: return@composable
                // Display the Portfolio screen.
                PortfolioScreen(
                    userId = userId,
                    // Navigate to the Search screen when the user initiates a search.
                    onNavigateToSearch = { navController.navigate(MainDest.SEARCH) }
                )
            }

            // Define the composable for the Search screen.
            composable(MainDest.SEARCH) {
                // Get the user ID from the session.
                val userId = SessionManager.getUserId() ?: return@composable
                // Display the Search screen.
                SearchScreen(onBack = { navController.popBackStack() },
                    // Navigate to the Details screen when a stock is selected.
                    onNavigateToDetails = {ticker: String -> navController.navigate(details(
                        ticker,
                        userId))})}
            
            // Define the composable for the Details screen, which takes ticker and userId as arguments.
            composable(
                route = MainDest.DETAILS,
                arguments = listOf(
                    navArgument("ticker") { type = NavType.StringType },
                    navArgument("userId"){type = NavType.IntType}
                )
            ) { backStackEntry ->
                // Extract the arguments from the back stack entry.
                val args = requireNotNull(backStackEntry.arguments)
                val ticker = requireNotNull(args.getString("ticker"))
                val userId = requireNotNull(args.getInt("userId"))
                // Display the Details screen with the provided arguments.
                DetailsScreen(ticker = ticker, userId = userId, onBack = { navController.navigate(MainDest.HOME) })
            }
            // Define the composable for the Watchlist screen.
            composable(MainDest.WATCHLIST) {
                // Get the user ID from the session.
                val userId = SessionManager.getUserId() ?: return@composable
                // Display the Watchlist screen.
                WatchListScreen(userId)
            }

            composable(AuthDest.RESET)
            {
                ForgotPasswordScreen()
            }

            // Define the composable for the Transaction screen.
            composable(MainDest.TRANSACTION) {
                // Get the user ID from the session.
                val userId = SessionManager.getUserId() ?: return@composable
                // Display the Transaction screen.
                TransactionScreen(userId = userId) }

            // Define the composable for the News screen.
            composable(MainDest.NEWS)
            {
                // Display the News screen.
                NewsScreen()
            }
        }
    }
}
