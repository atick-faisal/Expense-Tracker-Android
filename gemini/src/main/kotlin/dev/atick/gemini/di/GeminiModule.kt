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

package dev.atick.gemini.di

import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.HarmBlockThreshold
import com.google.firebase.ai.type.HarmCategory
import com.google.firebase.ai.type.SafetySetting
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.atick.gemini.data.GeminiRateLimiter
import dev.atick.gemini.data.GeminiRateLimiterImpl
import dev.atick.gemini.models.AiCurrencyType
import dev.atick.gemini.models.AiExpenseCategory
import dev.atick.gemini.models.AiPaymentStatus
import dev.atick.gemini.models.AiRecurringType
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Annotation class for the chat model.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ChatModel

/**
 * Annotation class for the expenses model.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ExpensesModel

/**
 * Dagger module that provides the binding for the [GenerativeModel] interface.
 */
@Module
@InstallIn(SingletonComponent::class)
object GeminiModule {

    /**
     * Provides the [GenerativeModel] for the expenses model.
     *
     * @return The [GenerativeModel] for the expenses model.
     */
    @Provides
    @Singleton
    @ExpensesModel
    fun provideGeminiClient(): GenerativeModel {
        return Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel(
                modelName = "gemini-2.5-flash-lite",
                generationConfig = generationConfig {
                    temperature = 0.15f
                    topK = 32
                    topP = 1f
                    maxOutputTokens = 256
                    responseMimeType = "application/json"
                    responseSchema = Schema.obj(
                        title = "expense",
                        description = "Extracted expense information from message",
                        properties = mapOf(
                            "amount" to Schema.double(
                                title = "amount",
                                description = "The expense amount in the original currency",
                                nullable = false,
                            ),
                            "currency" to Schema.enumeration(
                                title = "currency",
                                description = "The currency of the expense",
                                values = AiCurrencyType.entries.map { it.name },
                                nullable = false,
                            ),
                            "merchant" to Schema.string(
                                title = "merchant",
                                description = "Name of merchant of the expense",
                                nullable = false,
                            ),
                            "category" to Schema.enumeration(
                                title = "category",
                                description = "The category of the expense",
                                values = AiExpenseCategory.entries.map { it.name },
                                nullable = false,
                            ),
                            "paymentStatus" to Schema.enumeration(
                                title = "paymentStatus",
                                description = "The payment status of the expense",
                                values = AiPaymentStatus.entries.map { it.name },
                                nullable = false,
                            ),
                            "recurringType" to Schema.enumeration(
                                title = "recurringType",
                                description = "The recurring nature of the expense",
                                values = AiRecurringType.entries.map { it.name },
                                nullable = false,
                            ),
                            "paymentDate" to Schema.string(
                                title = "paymentDate",
                                description = "The date when the payment was made (ISO format: yyyy-MM-ddTHH:mm:ss.SSSSSS)",
                                nullable = false,
                            ),
                        ),
                    )
                },
                safetySettings = listOf(
                    SafetySetting(HarmCategory.HARASSMENT, HarmBlockThreshold.MEDIUM_AND_ABOVE),
                    SafetySetting(HarmCategory.HATE_SPEECH, HarmBlockThreshold.MEDIUM_AND_ABOVE),
                    SafetySetting(
                        HarmCategory.SEXUALLY_EXPLICIT,
                        HarmBlockThreshold.MEDIUM_AND_ABOVE,
                    ),
                    SafetySetting(
                        HarmCategory.DANGEROUS_CONTENT,
                        HarmBlockThreshold.MEDIUM_AND_ABOVE,
                    ),
                ),
            )
    }

    /**
     * Provides the [GenerativeModel] for the chat model.
     *
     * @return The [GenerativeModel] for the chat model.
     */
    @Provides
    @Singleton
    @ChatModel
    fun provideChatModel(): GenerativeModel {
        return Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel(
                modelName = "gemini-2.5-flash-lite",
                generationConfig = generationConfig {
                    temperature = 0.15f
                    topK = 32
                    topP = 1f
                    maxOutputTokens = 256
                },
                safetySettings = listOf(
                    SafetySetting(HarmCategory.HARASSMENT, HarmBlockThreshold.MEDIUM_AND_ABOVE),
                    SafetySetting(HarmCategory.HATE_SPEECH, HarmBlockThreshold.MEDIUM_AND_ABOVE),
                    SafetySetting(
                        HarmCategory.SEXUALLY_EXPLICIT,
                        HarmBlockThreshold.MEDIUM_AND_ABOVE,
                    ),
                    SafetySetting(
                        HarmCategory.DANGEROUS_CONTENT,
                        HarmBlockThreshold.MEDIUM_AND_ABOVE,
                    ),
                ),
            )
    }

    /**
     * Provides the [GeminiRateLimiter] for the Gemini API.
     *
     * @return The [GeminiRateLimiter] for the Gemini API.
     */
    @Provides
    @Singleton
    fun provideGeminiRateLimiter(): GeminiRateLimiter {
        return GeminiRateLimiterImpl()
    }
}
