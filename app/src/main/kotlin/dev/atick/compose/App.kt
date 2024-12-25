/*
 * Copyright 2023 Atick Faisal
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

package dev.atick.compose

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.atick.compose.sync.Sync
import dev.atick.core.extensions.hasPermission
import timber.log.Timber

/**
 * The main application class that extends [Application] and is annotated with [HiltAndroidApp].
 */
@HiltAndroidApp
class App : Application() {

    /**
     * Called when the application is first created.
     * Performs initialization tasks, such as setting up Timber logging in debug mode.
     */
    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        if (hasPermission(Manifest.permission.READ_SMS)) Sync.initialize(this)
    }
}
