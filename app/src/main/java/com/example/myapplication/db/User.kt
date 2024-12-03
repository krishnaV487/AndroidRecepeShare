package com.example.myapplication.db
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val userId: String,
    val firstName: String,
    val lastName: String,
    val email: String
)
