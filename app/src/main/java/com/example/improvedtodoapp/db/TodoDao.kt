package com.example.improvedtodoapp.db

import androidx.room.*
import com.example.improvedtodoapp.model.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: Todo)

    @Delete
    suspend fun deleteTodo(todo: Todo)

    @Query("SELECT * FROM todo")
    fun getTodos(): Flow<List<Todo>>
}