package com.example.pruebamaterial3.repository

import com.example.pruebamaterial3.db.TodoDao
import com.example.pruebamaterial3.model.Todo
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