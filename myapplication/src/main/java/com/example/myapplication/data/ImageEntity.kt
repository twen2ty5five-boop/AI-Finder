package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uri: String,
    val keywords: String, // Comma separated keywords
    val timestamp: Long
)
