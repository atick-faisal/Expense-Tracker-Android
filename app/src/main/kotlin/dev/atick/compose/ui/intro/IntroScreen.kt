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

package dev.atick.compose.ui.intro

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.atick.compose.R
import dev.atick.core.ui.utils.StatefulComposable
import kotlinx.coroutines.launch

data class OnboardingPage(
    @DrawableRes val imageRes: Int,
    @StringRes val title: Int,
    @StringRes val description: Int,
)

val onboardingPages = listOf(
    OnboardingPage(
        imageRes = R.mipmap.ic_launcher_foreground,
        title = R.string.app_name,
        description = R.string.your_smart_companion,
    ),
    OnboardingPage(
        imageRes = R.drawable.intro_expenses,
        title = R.string.smart_expense_tracking,
        description = R.string.automatically_capture,
    ),
    OnboardingPage(
        imageRes = R.drawable.intro_analysis,
        title = R.string.insightful_analysis,
        description = R.string.understand_your_spending,
    ),
    OnboardingPage(
        imageRes = R.drawable.intro_budget,
        title = R.string.budget_management,
        description = R.string.set_and_track,
    ),
    OnboardingPage(
        imageRes = R.drawable.intro_subscriptions,
        title = R.string.subscription_control,
        description = R.string.never_miss_a_subscription,
    ),
    OnboardingPage(
        imageRes = R.drawable.intro_chat,
        title = R.string.ai_financial_assistant,
        description = R.string.get_personalized_insights,
    ),
)

@Composable
internal fun IntroRoute(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    introViewModel: IntroViewModel = hiltViewModel(),
) {
    val introState by introViewModel.introUiState.collectAsStateWithLifecycle()

    StatefulComposable(
        state = introState,
        onShowSnackbar = onShowSnackbar,
    ) {
        OnboardingScreen(onFinish = introViewModel::userFinishedIntro)
    }
}

@Composable
private fun OnboardingScreen(
    onFinish: () -> Unit,
) {
    val pagerState = rememberPagerState { onboardingPages.size }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) { page ->
            OnboardingPage(
                page = onboardingPages[page],
                modifier = Modifier.fillMaxSize(),
                isFirstPage = page == 0,
            )
        }

        // Bottom section with indicators and buttons
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            // Page indicators
            Row(
                Modifier
                    .align(Alignment.CenterStart)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                repeat(onboardingPages.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    }
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(color, shape = MaterialTheme.shapes.small),
                    )
                }
            }

            // Navigation buttons
            Row(
                Modifier.align(Alignment.CenterEnd),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (pagerState.currentPage < onboardingPages.size - 1) {
                    TextButton(
                        onClick = {
                            // Skip to last page
                            scope.launch {
                                pagerState.animateScrollToPage(onboardingPages.size - 1)
                            }
                        },
                    ) {
                        Text(stringResource(R.string.skip))
                    }
                    Button(
                        onClick = {
                            // Move to next page
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                    ) {
                        Text(stringResource(R.string.next))
                    }
                } else {
                    Button(
                        onClick = onFinish,
                    ) {
                        Text(stringResource(R.string.get_started))
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingPage(
    page: OnboardingPage,
    modifier: Modifier = Modifier,
    isFirstPage: Boolean = false,
) {
    Column(
        modifier = modifier
            // .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(if (isFirstPage) 320.dp else 280.dp)
                .padding(bottom = if (isFirstPage) 48.dp else 32.dp),
            contentScale = ContentScale.Fit,
        )

        Text(
            text = stringResource(page.title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Text(
            text = stringResource(page.description),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        )
    }
}

@Preview
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen(onFinish = {})
}
