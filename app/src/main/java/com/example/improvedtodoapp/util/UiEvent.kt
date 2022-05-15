package com.example.improvedtodoapp.util

sealed class UiEvent {
    data class ShowSnackbar(
        val message: String,
        val action: String? = null
    ): UiEvent()
    data class ShowToast(
        val message: String
    ): UiEvent()
}