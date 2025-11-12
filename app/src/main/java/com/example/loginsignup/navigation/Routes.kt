package com.example.loginsignup.navigation

import android.net.Uri

object Graph {
    const val AUTH = "auth"
    const val MAIN = "main"
}

// Authentication destinations
object AuthDest {
    const val SIGN_UP = "signUp"
    const val LOGIN = "login"
    const val TERMS = "terms"
}

// Main (post-login) destinations
object MainDest {
    const val ROOT = "main_root"
    const val HOME = "home"

    const val DETAILS = "details/{ticker}/{userId}"
    const val WATCHLIST = "watchlist"
    const val PROFILE = "profile"
    const val SEARCH = "search"

    const val NEWS = "news"
    const val TRANSACTION = "transaction"

    fun details(ticker: String, userId: Int) = "details/${Uri.encode(ticker)}/${userId}"

}

