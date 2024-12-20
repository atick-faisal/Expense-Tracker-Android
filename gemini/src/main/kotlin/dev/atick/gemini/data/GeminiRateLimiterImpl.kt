/*
 * Copyright 2024 Atick Faisal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.atick.gemini.data

import kotlinx.coroutines.delay
import javax.inject.Inject

class GeminiRateLimiterImpl @Inject constructor() : GeminiRateLimiter {
    private val requestTimestamps = mutableListOf<Long>()

    override suspend fun checkAndDelay() {
        val currentTime = System.currentTimeMillis()

        // Remove old timestamps outside the window
        requestTimestamps.removeAll { it < currentTime - GeminiRateLimiter.WINDOW_MS }

        if (requestTimestamps.size >= GeminiRateLimiter.MAX_REQUESTS) {
            // Calculate required delay based on oldest request
            val oldestTimestamp = requestTimestamps.first()
            val delayNeeded = (GeminiRateLimiter.WINDOW_MS - (currentTime - oldestTimestamp))
            if (delayNeeded > 0) {
                delay(delayNeeded)
            }
        }

        requestTimestamps.add(currentTime)
    }
}
