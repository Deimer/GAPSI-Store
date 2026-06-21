package com.deymervilla.gapsistore.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AppRoutes : NavKey {

    @Serializable
    data object Splash : AppRoutes

    @Serializable
    data object Home : AppRoutes
}