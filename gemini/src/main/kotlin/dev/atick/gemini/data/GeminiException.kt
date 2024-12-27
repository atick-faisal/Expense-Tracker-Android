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

/**
 * A sealed class that represents the exceptions that can occur during a Gemini AI operation.
 *
 * @param message The message to display for the exception.
 * @param cause The cause of the exception.
 */
sealed class GeminiException(message: String? = null, cause: Throwable? = null) :
    Exception(message, cause) {
    /**
     * Represents an exception that occurs during serialization.
     *
     * @param message The message to display for the exception.
     * @param cause The cause of the exception.
     */
    class Serialization(message: String, cause: Throwable? = null) : GeminiException(message, cause)

    /**
     * Represents an exception that occurs on the server.
     *
     * @param message The message to display for the exception.
     * @param cause The cause of the exception.
     */
    class Server(message: String, cause: Throwable? = null) : GeminiException(message, cause)

    /**
     * Represents an exception that occurs due to an invalid API key.
     *
     * @param message The message to display for the exception.
     */
    class InvalidAPIKey(message: String) : GeminiException(message)

    /**
     * Represents an exception that occurs when a prompt is blocked.
     *
     * @param message The message to display for the exception.
     * @param cause The cause of the exception.
     */
    class PromptBlocked(message: String, cause: Throwable? = null) : GeminiException(message, cause)

    /**
     * Represents an exception that occurs when the user location is unsupported.
     *
     * @param cause The cause of the exception.
     */
    class UnsupportedUserLocation(cause: Throwable? = null) : GeminiException(cause = cause)

    /**
     * Represents an exception that occurs due to an invalid state.
     *
     * @param message The message to display for the exception.
     * @param cause The cause of the exception.
     */
    class InvalidState(message: String, cause: Throwable? = null) : GeminiException(message, cause)

    /**
     * Represents an exception that occurs when the response is stopped.
     *
     * @param message The message to display for the exception.
     * @param cause The cause of the exception.
     */
    class ResponseStopped(message: String, cause: Throwable? = null) :
        GeminiException(message, cause)

    /**
     * Represents an exception that occurs when the request times out.
     *
     * @param message The message to display for the exception.
     * @param cause The cause of the exception.
     */
    class RequestTimeout(message: String, cause: Throwable? = null) :
        GeminiException(message, cause)

    /**
     * Represents an exception that occurs when the quota is exceeded.
     *
     * @param message The message to display for the exception.
     * @param cause The cause of the exception.
     */
    class QuotaExceeded(message: String, cause: Throwable? = null) : GeminiException(message, cause)

    /**
     * Represents an exception that occurs when the error is unknown.
     *
     * @param message The message to display for the exception.
     * @param cause The cause of the exception.
     */
    class Unknown(message: String, cause: Throwable? = null) : GeminiException(message, cause)
}

/**
 * Converts a throwable to a Gemini exception.
 *
 * @return The Gemini exception.
 */
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
