package com.example.improvedtodoapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.improvedtodoapp.model.Todo

@Database(
    entities = [Todo::class],
    version = 1,
    exportSchema = false
)
abstract class TodoDatabase: RoomDatabase(){
    abstract val dao: TodoDao
}
