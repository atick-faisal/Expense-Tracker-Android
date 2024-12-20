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

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.FunctionType
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.generationConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.atick.gemini.BuildConfig
import dev.atick.gemini.data.GeminiRateLimiter
import dev.atick.gemini.data.GeminiRateLimiterImpl
import dev.atick.gemini.models.AiCurrencyType
import dev.atick.gemini.models.AiExpenseCategory
import dev.atick.gemini.models.AiPaymentStatus
import dev.atick.gemini.models.AiRecurringType
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ChatModel

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ExpensesModel

@Module
@InstallIn(SingletonComponent::class)
object GeminiModule {

    @Provides
    @Singleton
    @ExpensesModel
    fun provideGeminiClient(): GenerativeModel {
        return GenerativeModel(
            modelName = "gemini-1.5-flash-001",
            apiKey = BuildConfig.GEMINI_API_KEY,
            generationConfig = generationConfig {
                temperature = 0.15f
                topK = 32
                topP = 1f
                maxOutputTokens = 4096
                responseMimeType = "application/json"
                responseSchema = Schema(
                    name = "expense",
                    description = "Extracted expense information from message",
                    type = FunctionType.OBJECT,
                    properties = mapOf(
                        "amount" to Schema.double(
                            name = "amount",
                            description = "The expense amount in the original currency",
                        ),
                        "currency" to Schema.enum(
                            name = "currency",
                            description = "The currency of the expense",
                            values = AiCurrencyType.entries.map { it.name },
                        ),
                        "category" to Schema.enum(
                            name = "category",
                            description = "The category of the expense",
                            values = AiExpenseCategory.entries.map { it.name },
                        ),
                        "paymentStatus" to Schema.enum(
                            name = "paymentStatus",
                            description = "The payment status of the expense",
                            values = AiPaymentStatus.entries.map { it.name },
                        ),
                        "recurringType" to Schema.enum(
                            name = "recurringType",
                            description = "The recurring nature of the expense",
                            values = AiRecurringType.entries.map { it.name },
                        ),
                        "paymentDate" to Schema.str(
                            name = "paymentDate",
                            description = "The date when the payment was made (ISO format: yyyy-MM-dd')",
                        ),
                        "description" to Schema.str(
                            name = "description",
                            description = "Description or merchant name of the expense",
                        ),
                    ),
                    required = listOf("amount", "currency", "category", "paymentStatus", "recurringType"),
                )
            },
            safetySettings = listOf(
                SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE),
            ),
        )
    }

    @Provides
    @Singleton
    @ChatModel
    fun provideChatModel(): GenerativeModel {
        return GenerativeModel(
            modelName = "gemini-1.5-flash-001",
            apiKey = BuildConfig.GEMINI_API_KEY,
            generationConfig = generationConfig {
                temperature = 0.15f
                topK = 32
                topP = 1f
                maxOutputTokens = 4096
            },
            safetySettings = listOf(
                SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE),
            ),
        )
    }

    @Provides
    @Singleton
    fun provideGeminiRateLimiter(): GeminiRateLimiter {
        return GeminiRateLimiterImpl()
    }
}
