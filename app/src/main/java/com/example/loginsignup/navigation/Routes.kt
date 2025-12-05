// Package declaration for the navigation
package com.example.loginsignup.navigation

// Import statement for Uri
import android.net.Uri

/**
 * Object defining the navigation graphs.
 */
object Graph {
    // A constant for the authentication graph.
    const val AUTH = "auth"
    // A constant for the main graph (post-login).
    const val MAIN = "main"
}

/**
 * Object defining the authentication destinations.
 */
object AuthDest {
    // A constant for the sign-up screen route.
    const val SIGN_UP = "signUp"
    // A constant for the login screen route.
    const val LOGIN = "login"
    // A constant for the terms and conditions screen route.
    const val TERMS = "terms"

    const val RESET = "forgotPassword"
}

/**
 * Object defining the main (post-login) destinations.
 */
object MainDest {
    // A constant for the root of the main graph.
    const val ROOT = "main_root"
    // A constant for the home screen (Portfolio) route.
    const val HOME = "home"

    // A constant for the details screen route, with ticker and userId arguments.
    const val DETAILS = "details/{ticker}/{userId}"
    // A constant for the watchlist screen route.
    const val WATCHLIST = "watchlist"
    // A constant for the profile screen route.
    const val PROFILE = "profile"
    // A constant for the search screen route.
    const val SEARCH = "search"

    // A constant for the news screen route.
    const val NEWS = "news"
    // A constant for the transaction screen route.
    const val TRANSACTION = "transaction"

    /**
     * Creates the route for the details screen.
     *
     * @param ticker The stock ticker.
     * @param userId The ID of the user.
     * @return The route for the details screen.
     */
    fun details(ticker: String, userId: Int) = "details/${Uri.encode(ticker)}/${userId}"

}
