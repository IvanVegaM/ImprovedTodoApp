package com.example.pruebamaterial3.util

sealed class UiEvent {
    data class ShowSnackbar(
        val message: String,
        val action: String? = null
    ): UiEvent()
    data class ShowToast(
        val message: String
    ): UiEvent()
}