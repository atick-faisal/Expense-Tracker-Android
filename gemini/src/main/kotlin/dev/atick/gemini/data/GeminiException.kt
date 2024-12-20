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

import com.google.ai.client.generativeai.type.GoogleGenerativeAIException
import com.google.ai.client.generativeai.type.InvalidAPIKeyException
import com.google.ai.client.generativeai.type.InvalidStateException
import com.google.ai.client.generativeai.type.PromptBlockedException
import com.google.ai.client.generativeai.type.QuotaExceededException
import com.google.ai.client.generativeai.type.RequestTimeoutException
import com.google.ai.client.generativeai.type.ResponseStoppedException
import com.google.ai.client.generativeai.type.SerializationException
import com.google.ai.client.generativeai.type.ServerException
import com.google.ai.client.generativeai.type.UnsupportedUserLocationException
import kotlinx.coroutines.TimeoutCancellationException

sealed class GeminiException(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {
    class Serialization(message: String, cause: Throwable? = null) : GeminiException(message, cause)
    class Server(message: String, cause: Throwable? = null) : GeminiException(message, cause)
    class InvalidAPIKey(message: String) : GeminiException(message)
    class PromptBlocked(message: String, cause: Throwable? = null) : GeminiException(message, cause)
    class UnsupportedUserLocation(cause: Throwable? = null) : GeminiException(cause = cause)
    class InvalidState(message: String, cause: Throwable? = null) : GeminiException(message, cause)
    class ResponseStopped(message: String, cause: Throwable? = null) : GeminiException(message, cause)
    class RequestTimeout(message: String, cause: Throwable? = null) : GeminiException(message, cause)
    class QuotaExceeded(message: String, cause: Throwable? = null) : GeminiException(message, cause)
    class Unknown(message: String, cause: Throwable? = null) : GeminiException(message, cause)
}

fun Throwable.toGeminiException(): GeminiException {
    return when (this) {
        is GoogleGenerativeAIException -> when (this) {
            is SerializationException -> GeminiException.Serialization(message ?: "", cause)
            is ServerException -> GeminiException.Server(message ?: "", cause)
            is InvalidAPIKeyException -> GeminiException.InvalidAPIKey(message ?: "")
            is PromptBlockedException -> GeminiException.PromptBlocked(message ?: "", cause)
            is UnsupportedUserLocationException -> GeminiException.UnsupportedUserLocation(cause)
            is InvalidStateException -> GeminiException.InvalidState(message ?: "", cause)
            is ResponseStoppedException -> GeminiException.ResponseStopped(message ?: "", cause)
            is RequestTimeoutException -> GeminiException.RequestTimeout(message ?: "", cause)
            is QuotaExceededException -> GeminiException.QuotaExceeded(message ?: "", cause)
            else -> GeminiException.Unknown(message ?: "Unknown error occurred", cause)
        }
        is TimeoutCancellationException ->
            GeminiException.RequestTimeout("The request failed to complete in the allotted time.")
        else -> GeminiException.Unknown("Something unexpected happened.", this)
    }
}
