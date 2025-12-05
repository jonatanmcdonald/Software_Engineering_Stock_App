package com.example.loginsignup.utils

import android.os.SystemClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.max

// This class provides a global rate limiter for API calls.
class GlobalRateLimiter(maxPerMinute: Int) {
    private val gapMs = 60_000L / maxPerMinute // The gap in milliseconds between API calls.
    private val mutex = Mutex() // A mutex to ensure thread safety.
    private var nextAllowedAt = 0L // The timestamp when the next API call is allowed.

    // This function runs a block of code after waiting for the rate limiter.
    suspend fun <T> run(block: suspend () -> T): T {
        mutex.withLock { // Acquires the mutex.
            val now = SystemClock.elapsedRealtime() // Gets the current time.
            val wait = max(0L, nextAllowedAt - now) // Calculates the wait time.
            if (wait > 0) delay(wait) // Delays for the wait time.
            nextAllowedAt = max(now, nextAllowedAt) + gapMs // Updates the next allowed timestamp.
        }
        return block() // Executes the block of code.
    }
}
