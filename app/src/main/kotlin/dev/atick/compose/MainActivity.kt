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
import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.compose.ui.JetpackApp
import dev.atick.core.ui.extensions.checkForPermissions
import dev.atick.core.ui.extensions.collectWithLifecycle
import dev.atick.core.ui.theme.JetpackTheme
import dev.atick.core.ui.utils.UiState
import dev.atick.core.ui.utils.setLanguagePreference
import dev.atick.network.utils.NetworkUtils
import dev.atick.storage.preferences.models.DarkThemeConfig
import dev.atick.storage.preferences.models.ThemeBrand
import dev.atick.storage.preferences.models.UserData
import timber.log.Timber
import javax.inject.Inject

/**
 * Main activity for the application.
 */
// Switched to AppCompatActivity Temporarily
// https://developer.android.com/guide/topics/resources/app-languages#androidx-impl
// class MainActivity : ComponentActivity() {
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val permissions = mutableListOf<String>()

    @Inject
    lateinit var networkUtils: NetworkUtils

    private val viewModel: MainActivityViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var uiState: UiState<UserData> by mutableStateOf(UiState(UserData(), loading = true))

        collectWithLifecycle(viewModel.uiState) { uiState = it }

        // Keep the splash screen on-screen until the UI state is loaded. This condition is
        // evaluated each time the app needs to be redrawn so it should be fast to avoid blocking
        // the UI.
        splashScreen.setKeepOnScreenCondition { uiState.loading }

        // Turn off the decor fitting system windows, which allows us to handle insets,
        // including IME animations, and go edge-to-edge
        // This also sets up the initial system bar style based on the platform theme
        enableEdgeToEdge()

        setContent {
            val darkTheme = shouldUseDarkTheme(uiState)

            // Update the edge to edge configuration to match the theme
            // This is the same parameters as the default enableEdgeToEdge call, but we manually
            // resolve whether or not to show dark theme using uiState, since it can be different
            // than the configuration's dark theme value based on the user preference.
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim,
                        darkScrim,
                    ) { darkTheme },
                )
                onDispose {}
            }

            // Set the locale for the application based on the user's preferred language
            LaunchedEffect(uiState.data.language) {
                setLanguagePreference(uiState)
            }

            JetpackTheme(
                darkTheme = darkTheme,
                androidTheme = shouldUseAndroidTheme(uiState),
                disableDynamicTheming = shouldDisableDynamicTheming(uiState),
            ) {
                JetpackApp(
                    userOnboarded = isOnboardingComplete(uiState),
                    networkUtils = networkUtils,
                    windowSizeClass = calculateWindowSizeClass(this),
                )
            }
        }

        // Configure required permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        permissions.add(Manifest.permission.RECEIVE_SMS)
        permissions.add(Manifest.permission.READ_SMS)

        // Check for permissions and launch Bluetooth enable request
        checkForPermissions(permissions) {}
    }
}

/**
 * Returns `true` if the Android theme should be used, as a function of the [uiState].
 */
@Composable
private fun shouldUseAndroidTheme(
    uiState: UiState<UserData>,
): Boolean {
    if (uiState.loading || uiState.error.peekContent() != null) return false

    return when (uiState.data.themeBrand) {
        ThemeBrand.ANDROID -> true
        ThemeBrand.DEFAULT -> false
    }
}

/**
 * Returns `true` if the dynamic color is disabled, as a function of the [uiState].
 */
@Composable
private fun shouldDisableDynamicTheming(
    uiState: UiState<UserData>,
): Boolean {
    return if (uiState.loading || uiState.error.peekContent() != null) {
        false
    } else {
        !uiState.data.useDynamicColor
    }
}

/**
 * Returns `true` if dark theme should be used, as a function of the [uiState] and the
 * current system context.
 */
@Composable
private fun shouldUseDarkTheme(
    uiState: UiState<UserData>,
): Boolean {
    return if (uiState.loading || uiState.error.peekContent() != null) {
        isSystemInDarkTheme()
    } else {
        when (uiState.data.darkThemeConfig) {
            DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
            DarkThemeConfig.LIGHT -> false
            DarkThemeConfig.DARK -> true
        }
    }
}

/**
 * Determines whether a user is logged in based on the provided [UiState].
 *
 * @param uiState The UI state representing the user data.
 * @return `true` if the user is considered logged in; `false` otherwise.
 */
private fun isOnboardingComplete(uiState: UiState<UserData>): Boolean {
    // User is considered logged in during loading (assuming ongoing session).
    return uiState.data.id.isNotEmpty() || uiState.loading
}

/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)

/**
 * Sets the locale for the application based on the user's preferred language.
 *
 * @param uiState The UI state representing the user data.
 */
private fun setLanguagePreference(uiState: UiState<UserData>) {
    Timber.d("Setting language preference to: ${uiState.data.language}")
    setLanguagePreference(uiState.data.language)
}
