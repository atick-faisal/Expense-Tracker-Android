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

package dev.atick.sms.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.atick.sms.data.SMSDataSource
import dev.atick.sms.data.SMSDataSourceImpl
import javax.inject.Singleton

/**
 * Dagger module that provides the binding for the [SMSDataSource] interface.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    /**
     * Binds the [SMSDataSourceImpl] implementation to the [SMSDataSource] interface.
     *
     * @param smsDataSourceImpl The implementation of [SMSDataSource] to be bound.
     * @return The [SMSDataSource] interface.
     */
    @Binds
    @Singleton
    abstract fun bindSMSDataSource(
        smsDataSourceImpl: SMSDataSourceImpl,
    ): SMSDataSource
}
