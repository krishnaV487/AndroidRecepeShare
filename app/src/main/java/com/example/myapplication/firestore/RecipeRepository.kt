package com.example.myapplication.firestore

import android.content.Context
import com.example.myapplication.db.AppDb
import com.example.myapplication.db.Recipe
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RecipeRepository(context: Context) {

    private val roomDb = AppDb.getInstance(context)
    private val firestore = FirebaseFirestore.getInstance()

    // Upload local recipes to Firestore
    suspend fun syncLocalRecipesToFirestore(userId: String) = withContext(Dispatchers.IO) {
        try {
            val localRecipes = roomDb.recipeDao().getRecipesByUser(userId)
            localRecipes.forEach { recipe ->
                firestore.collection("recipes").document(recipe.recipeId).set(recipe).await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Fetch recipes from Firestore and cache in Room
    suspend fun fetchRecipesFromFirestore(userId: String) = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection("recipes")
                .whereEqualTo("createdBy", userId)
                .get()
                .await()

            val recipes = snapshot.documents.mapNotNull { document ->
                document.toObject(Recipe::class.java)?.copy(recipeId = document.id)
            }

            // Cache in Room
            roomDb.recipeDao().deleteAllRecipes()
            roomDb.recipeDao().insertAll(recipes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getRecipeById(recipeId: String): Recipe? = withContext(Dispatchers.IO) {
        roomDb.recipeDao().getRecipeById(recipeId)
    }


    // Retrieve locally stored recipes for offline access
    suspend fun getLocalRecipes(userId: String): List<Recipe> = withContext(Dispatchers.IO) {
        roomDb.recipeDao().getRecipesByUser(userId)
    }

    // Insert a new recipe locally and sync to Firestore
    suspend fun insertRecipe(recipe: Recipe) = withContext(Dispatchers.IO) {
        try {
            roomDb.recipeDao().insertRecipe(recipe)
            firestore.collection("recipes").document(recipe.recipeId).set(recipe).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
