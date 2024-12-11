package com.example.myapplication.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecipeDao {

    // Insert a single recipe
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe)

    // Bulk insert for multiple recipes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<Recipe>)

    // Get all recipes for a specific user
    @Query("SELECT * FROM recipes WHERE createdBy = :userId")
    suspend fun getRecipesByUser(userId: String): List<Recipe>

    // Get all recipes
    @Query("SELECT * FROM recipes")
    suspend fun getAllRecipes(): List<Recipe>

    // Get a single recipe by its ID
    @Query("SELECT * FROM recipes WHERE recipeId = :id")
    suspend fun getRecipeById(id: String): Recipe?

    // Delete all recipes (used when clearing cache)
    @Query("DELETE FROM recipes")
    suspend fun deleteAllRecipes()

    @Query("DELETE FROM recipes WHERE recipeId = :recipeId")
    suspend fun deleteRecipeById(recipeId: String)

    // Delete recipes for a specific user
    @Query("DELETE FROM recipes WHERE createdBy = :userId")
    suspend fun deleteRecipesByUser(userId: String)
}
