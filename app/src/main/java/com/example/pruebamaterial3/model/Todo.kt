package com.example.pruebamaterial3.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Todo(
    val description: String,
    val isDone: Boolean,
    @PrimaryKey val id: Int? = null
)
