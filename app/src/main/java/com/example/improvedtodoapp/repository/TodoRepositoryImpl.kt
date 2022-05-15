package com.example.improvedtodoapp.repository

import com.example.improvedtodoapp.db.TodoDao
import com.example.improvedtodoapp.model.Todo
import kotlinx.coroutines.flow.Flow

class TodoRepositoryImpl(
    private val dao: TodoDao
): TodoRepository {
    override suspend fun insertTodo(todo: Todo) {
        dao.insertTodo(todo)
    }

    override suspend fun deleteTodo(todo: Todo) {
        dao.deleteTodo(todo)
    }

    override fun getTodos(): Flow<List<Todo>> {
        return dao.getTodos()
    }

}