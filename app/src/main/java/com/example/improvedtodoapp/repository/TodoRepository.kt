package com.example.improvedtodoapp.repository

import com.example.improvedtodoapp.model.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    suspend fun insertTodo(todo: Todo)

    suspend fun deleteTodo(todo: Todo)

    fun getTodos(): Flow<List<Todo>>
}