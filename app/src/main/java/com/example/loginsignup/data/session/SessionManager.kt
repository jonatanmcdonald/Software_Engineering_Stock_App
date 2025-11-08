package com.example.loginsignup.data.session

object SessionManager {
    private var userId: Int? = null
    private var isSignedIn: Boolean = false

    fun startSession(id:Int) {
        userId = id
        isSignedIn = true
    }

    fun endSession() {
        userId = null
        isSignedIn = false;
    }

    fun getUserId(): Int? = userId
    fun isUserSignedIn(): Boolean = isSignedIn
}