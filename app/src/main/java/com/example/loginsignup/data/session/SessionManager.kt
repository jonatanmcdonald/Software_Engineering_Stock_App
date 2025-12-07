package com.example.loginsignup.data.session

// This object manages the user's session.
object SessionManager {
    private var userId: Int? = null // The ID of the currently signed-in user.
    private var isSignedIn: Boolean = false // Whether a user is currently signed in.

    // This function starts a new session for a user.
    fun startSession(id:Int) {
        userId = id
        isSignedIn = true
    }

    // This function ends the current session.
    fun endSession() {
        userId = null
        isSignedIn = false;
    }

    // This function returns the ID of the currently signed-in user.
    fun getUserId(): Int? = userId
    // This function returns whether a user is currently signed in.
    fun isUserSignedIn(): Boolean = isSignedIn
}