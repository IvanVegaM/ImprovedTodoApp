package com.example.improvedtodoapp.ui.list_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.improvedtodoapp.model.Todo
import com.example.improvedtodoapp.repository.TodoRepository
import com.example.improvedtodoapp.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodosViewModel @Inject constructor(
    private val repository: TodoRepository
): ViewModel() {
    val todos = repository.getTodos()

    var todo by mutableStateOf<Todo?>(null)
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _uiState = mutableStateOf<TodosUiState>(TodosUiState.ListOnFocus)
    val uiState: State<TodosUiState> = _uiState

    private var deletedTodo: Todo? = null

    var firstDescription by mutableStateOf(todo?.description ?: "")
        private set

    var description by mutableStateOf(todo?.description ?: "")
        private set

    var isDone by mutableStateOf(false)
        private set

    var tfv by mutableStateOf( TextFieldValue() )
        private set

    fun onEvent(event: TodosUiEvent){
        when(event){
            TodosUiEvent.OnAddTodo -> {
                todo = null
                description = ""
                isDone = false
                tfv = TextFieldValue()
                _uiState.value = TodosUiState.NewTodo
            }
            is TodosUiEvent.OnSaveTodo -> {
                viewModelScope.launch {
                    if(event.todo.description.isBlank()) {
                        sendUiEvent(UiEvent.ShowSnackbar(
                            message = "The todo must have something to be saved"
                        ))
                        _uiState.value = TodosUiState.ListOnFocus
                        return@launch
                    }

                    if(_uiState.value == TodosUiState.NewTodo){
                        repository.insertTodo(event.todo)
                        //sendUiEvent(UiEvent.ShowSnackbar(
                        //    message = "Todo added successfully"
                        //))
                    }
                    else {
                        if(description == firstDescription){
                            _uiState.value = TodosUiState.ListOnFocus
                            return@launch
                        }
                        repository.insertTodo(
                            event.todo.copy(
                                description = description,
                                isDone = isDone,
                                id = todo?.id!!
                            )
                        )
                        sendUiEvent(UiEvent.ShowSnackbar(
                            message = "Todo edited successfully"
                        ))
                    }
                    _uiState.value = TodosUiState.ListOnFocus
                }
            }
            is TodosUiEvent.OnDeleteTodoClick -> {
                viewModelScope.launch{
                    deletedTodo = event.todo
                    repository.deleteTodo(event.todo)
                    _uiState.value = TodosUiState.ListOnFocus
                    sendUiEvent(UiEvent.ShowSnackbar(
                        message = "Todo deleted",
                        action = "Undo"
                    ))
                }
            }
            is TodosUiEvent.OnDoneChange -> {
                viewModelScope.launch{
                    repository.insertTodo(
                        event.todo.copy(
                            isDone = event.isDone
                        )
                    )
                }
            }
            TodosUiEvent.OnUndoDeleteClick -> {
                deletedTodo?.let{ todo ->
                    viewModelScope.launch{
                        repository.insertTodo(todo)
                    }
                }
            }
            is TodosUiEvent.OnDescriptionChange -> {
                description = event.description
                tfv = event.tfv
            }
            is TodosUiEvent.OnEditTodoClick -> {
                todo = event.todo
                firstDescription = todo?.description!!
                description = todo?.description!!
                isDone = todo?.isDone!!
                tfv = tfv.copy(text = todo?.description!!, selection = TextRange(todo?.description!!.length))
                _uiState.value = TodosUiState.EditTodo
            }
            TodosUiEvent.OnFocusList -> {
                _uiState.value = TodosUiState.ListOnFocus
            }
        }
    }

    private fun sendUiEvent(event: UiEvent){
        viewModelScope.launch{
            _uiEvent.send(event)
        }
    }
}