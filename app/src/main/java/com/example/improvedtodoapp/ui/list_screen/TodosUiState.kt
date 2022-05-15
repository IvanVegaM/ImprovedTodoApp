package com.example.improvedtodoapp.ui.list_screen

sealed class TodosUiState {
    object NewTodo: TodosUiState()
    object ListOnFocus: TodosUiState()
    object EditTodo: TodosUiState()
}