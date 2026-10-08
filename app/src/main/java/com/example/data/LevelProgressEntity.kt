package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "level_progress")
data class LevelProgressEntity(
    @PrimaryKey
    val levelNumber: Int,
    val isCompleted: Boolean,
    val stars: Int,
    val bestMoves: Int,
    val updatedAt: Long = System.currentTimeMillis()
)
