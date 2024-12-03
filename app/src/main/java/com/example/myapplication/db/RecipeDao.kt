package com.example.myapplication.db
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RecipeDao {
    @Insert
    suspend fun insertRecipe(recipe: Recipe)

    @Query("SELECT * FROM recipes WHERE recipeId = :id")
    suspend fun getRecipeById(id: String): Recipe?

    @Query("DELETE FROM recipes")
    suspend fun deleteAllRecipes()
}
