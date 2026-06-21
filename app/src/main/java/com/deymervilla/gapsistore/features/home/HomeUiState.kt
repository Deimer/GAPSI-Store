package com.deymervilla.gapsistore.features.home

sealed interface HomeUiState {
    data object Idle : HomeUiState
    data object Loading : HomeUiState
    data object Success : HomeUiState
    data object ConnectionError : HomeUiState
    data class Error(val message: String) : HomeUiState
}