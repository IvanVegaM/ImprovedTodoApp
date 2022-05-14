package com.example.pruebamaterial3.ui.list_screen

import com.example.pruebamaterial3.model.Todo

sealed class TodosUiState {
    object NewTodo: TodosUiState()
    object ListOnFocus: TodosUiState()
    object EditTodo: TodosUiState()
}