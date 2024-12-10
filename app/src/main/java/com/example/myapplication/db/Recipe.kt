package com.example.myapplication.db

import android.util.MutableBoolean
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey val recipeId: String = "",
    val title: String = "",
    val description: String = "",
    @get:PropertyName("isPublic") @set:PropertyName("isPublic")
    var isPublic: Boolean = true,
    val groupIdList: List<String> = emptyList(),
    val userIdList: List<String> = emptyList(),
    val createdBy: String = "",
    val timestamp: Long = 0L,
    val recipeImage: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val steps: List<String> = emptyList()
) {
    // No-argument constructor for Firestore deserialization
    constructor() : this(
        recipeId = "",
        title = "",
        description = "",
        isPublic = false,
        groupIdList = emptyList(),
        userIdList = emptyList(),
        createdBy = "",
        timestamp = 0L,
        recipeImage = "",
        ingredients = emptyList(),
        steps = emptyList()
    )
}

data class Ingredient(
    val name: String ="",
    val amount: String =""
) {
    // Default constructor required by Firestore
    constructor() : this("", "")
}
