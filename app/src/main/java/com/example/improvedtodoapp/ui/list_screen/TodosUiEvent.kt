package com.example.improvedtodoapp.ui.list_screen

import androidx.compose.ui.text.input.TextFieldValue
import com.example.improvedtodoapp.model.Todo

sealed class TodosUiEvent {
    data class OnDeleteTodoClick(val todo: Todo): TodosUiEvent()
    data class OnDoneChange(val todo: Todo, val isDone: Boolean): TodosUiEvent()
    object OnUndoDeleteClick: TodosUiEvent()
    object OnAddTodo: TodosUiEvent()
    data class OnSaveTodo(val todo: Todo): TodosUiEvent()
    data class OnDescriptionChange(val description: String, val tfv: TextFieldValue): TodosUiEvent()
    data class OnEditTodoClick(val todo: Todo): TodosUiEvent()
    object OnFocusList: TodosUiEvent()
}
