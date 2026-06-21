package com.deymervilla.gapsistore.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.deymervilla.ds.screens.SplashScreenCompose
import com.deymervilla.gapsistore.features.home.HomeScreenCompose

@Composable
fun AppNavigation() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                BodyCompose()
            }
        }
    )
}

@Composable
private fun BodyCompose() {
    val backStack = rememberNavBackStack(AppRoutes.Splash)
    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<AppRoutes.Splash> {
                SplashScreenCompose(
                    onNavigateToHome = {
                        backStack.removeLastOrNull()
                        backStack.add(AppRoutes.Home)
                    }
                )
            }
            entry<AppRoutes.Home> {
                HomeScreenCompose()
            }
        }
    )
}