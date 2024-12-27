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

package dev.atick.sms.models

/**
 * Data class that represents an SMS message.
 *
 * @param id The unique identifier of the SMS message.
 * @param address The address of the sender or recipient of the SMS message.
 * @param body The body of the SMS message.
 * @param date The date and time the SMS message was sent or received.
 * @param type The type of the SMS message.
 */
data class SMSMessage(
    val id: Long,
    val address: String,
    val body: String,
    val date: Long,
    val type: Int,
)
