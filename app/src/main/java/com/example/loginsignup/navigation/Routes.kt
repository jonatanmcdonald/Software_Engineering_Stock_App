package com.example.loginsignup.navigation

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
    const val DETAILS = "details"
    const val WATCHLIST = "watchlist"
    const val PROFILE = "profile"

    const val PORTFOLIO = "portfolio"

    const val TRANSACTIONS = "transaction"
}