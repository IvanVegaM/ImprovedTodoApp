package com.example.pruebamaterial3.repository

import com.example.pruebamaterial3.model.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    suspend fun insertTodo(todo: Todo)

    suspend fun deleteTodo(todo: Todo)

    fun getTodos(): Flow<List<Todo>>
}