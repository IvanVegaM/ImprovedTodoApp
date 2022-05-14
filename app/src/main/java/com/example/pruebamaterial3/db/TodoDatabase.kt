package com.example.pruebamaterial3.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pruebamaterial3.model.Todo

@Database(
    entities = [Todo::class],
    version = 1,
    exportSchema = false
)
abstract class TodoDatabase: RoomDatabase(){
    abstract val dao: TodoDao
}
