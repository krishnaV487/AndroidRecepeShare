package com.example.myapplication.db
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey val recipeId: String,
    val title: String,
    val description: String,
    val isPublic: Boolean,
    val groupId: String?, // Null if the recipe is public
    val createdBy: String, // userId of the creator
    val timestamp: Long // For conflict resolution and sorting
)
