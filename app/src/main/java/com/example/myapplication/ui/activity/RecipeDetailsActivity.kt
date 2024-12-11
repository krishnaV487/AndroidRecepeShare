package com.example.myapplication.ui.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.db.Recipe
import com.example.myapplication.firestore.RecipeRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RecipeDetailsActivity : AppCompatActivity() {

    private lateinit var recipeRepository: RecipeRepository
    private val firestore = FirebaseFirestore.getInstance()

    private lateinit var recipeTitle: TextView
    private lateinit var recipeImageView: ImageView
    private lateinit var recipeDescription: TextView
    private lateinit var ingredientsContainer: LinearLayout
    private lateinit var stepsContainer: LinearLayout
    private lateinit var sharedGroupsContainer: LinearLayout

    private var recipeId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        recipeRepository = RecipeRepository(this)

        // Bind UI components
        recipeTitle = findViewById(R.id.recipeTitle)
        recipeImageView = findViewById(R.id.recipeImageView)
        recipeDescription = findViewById(R.id.recipeDescription)
        ingredientsContainer = findViewById(R.id.ingredientsContainer)
        stepsContainer = findViewById(R.id.stepsContainer)
        sharedGroupsContainer = findViewById(R.id.sharedGroupsContainer)

        recipeId = intent.getStringExtra("recipeId")
        if (recipeId != null) {
            loadRecipeDetails(recipeId!!)
        } else {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadRecipeDetails(recipeId: String) {
        lifecycleScope.launch {
            try {
                val recipe = recipeRepository.getRecipeById(recipeId)
                if (recipe != null) {
                    displayRecipeDetails(recipe)
                } else {
                    Toast.makeText(this@RecipeDetailsActivity, "Recipe not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@RecipeDetailsActivity, "Error loading recipe", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayRecipeDetails(recipe: Recipe) {
        // Set Title and Description
        recipeTitle.text = recipe.title
        recipeDescription.text = recipe.description

        // Load Recipe Image
        if (recipe.recipeImage.isNotEmpty()) {
            Glide.with(this).load(recipe.recipeImage).into(recipeImageView)
        } else {
            recipeImageView.setImageResource(R.drawable.placeholder_image)
        }

        // Display Ingredients
        ingredientsContainer.removeAllViews()
        recipe.ingredients.forEach { ingredient ->
            val textView = TextView(this)
            textView.text = "- ${ingredient.name}: ${ingredient.amount}"
            ingredientsContainer.addView(textView)
        }

        // Display Steps
        stepsContainer.removeAllViews()
        recipe.steps.forEachIndexed { index, step ->
            val textView = TextView(this)
            textView.text = "${index + 1}. $step"
            stepsContainer.addView(textView)
        }

        // Fetch Shared Groups and Users
        loadSharedGroupsAndUsers(recipe.userIdList, recipe.groupIdList)
    }

    private fun loadSharedGroupsAndUsers(userIds: List<String>, groupIds: List<String>) {
        lifecycleScope.launch {
            sharedGroupsContainer.removeAllViews()

            // Fetch Users
            val users = userIds.mapNotNull { userId ->
                val snapshot = firestore.collection("users").document(userId).get().await()
                snapshot.getString("firstName") + " " + snapshot.getString("lastName")
            }

            users.forEach { user ->
                val textView = TextView(this@RecipeDetailsActivity)
                textView.text = "User: $user"
                sharedGroupsContainer.addView(textView)
            }

            // Fetch Groups
            val groups = groupIds.mapNotNull { groupId ->
                val snapshot = firestore.collection("groups").document(groupId).get().await()
                snapshot.getString("groupName")
            }

            groups.forEach { group ->
                val textView = TextView(this@RecipeDetailsActivity)
                textView.text = "Group: $group"
                sharedGroupsContainer.addView(textView)
            }
        }
    }
}
