package com.example.loginsignup.utils

import android.os.SystemClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.max

class GlobalRateLimiter(maxPerMinute: Int) {
    private val gapMs = 60_000L / maxPerMinute
    private val mutex = Mutex()
    private var nextAllowedAt = 0L

    suspend fun <T> run(block: suspend () -> T): T {
        mutex.withLock {
            val now = SystemClock.elapsedRealtime()
            val wait = max(0L, nextAllowedAt - now)
            if (wait > 0) delay(wait)
            nextAllowedAt = max(now, nextAllowedAt) + gapMs
        }
        return block()
    }
}
