package com.example.myapplication

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ItemRecipeBinding
import com.example.myapplication.db.Recipe

class RecipeAdapter(
    private val currentUserId: String,
    private val onEditClick: (Recipe) -> Unit,
    private val onDeleteClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    private var recipes = listOf<Recipe>()

    fun submitList(newRecipes: List<Recipe>) {
        recipes = newRecipes
        notifyDataSetChanged()
        if (recipes.isEmpty()) {
            Log.d("RecipeAdapter", "No recipes to display.")
        } else {
            Log.d("RecipeAdapter", "Loaded ${recipes.size} recipes.")
        }
    }

    class RecipeViewHolder(private val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            recipe: Recipe,
            currentUserId: String,
            onEditClick: (Recipe) -> Unit,
            onDeleteClick: (Recipe) -> Unit
        ) {
            // Set image, title, and description
            binding.recipeTitle.text = recipe.title
            binding.recipeDescription.text = recipe.description.take(50) + "..."
            binding.recipeTimestamp.text = getTimeAgo(recipe.timestamp)

            if (recipe.recipeImage.isNotBlank()) {
                Glide.with(binding.root.context)
                    .load(recipe.recipeImage)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(binding.recipeImage)
            } else {
                binding.recipeImage.setImageResource(R.drawable.placeholder_image)
            }

            // Conditionally show Edit/Delete buttons
            if (recipe.createdBy == currentUserId) {
                binding.editButton.visibility = View.VISIBLE
                binding.deleteButton.visibility = View.VISIBLE

                // Edit button click
                binding.editButton.setOnClickListener {
                    onEditClick(recipe)
                }

                // Delete button click
                binding.deleteButton.setOnClickListener {
                    onDeleteClick(recipe)
                }
            } else {
                binding.editButton.visibility = View.GONE
                binding.deleteButton.visibility = View.GONE
            }
        }

        private fun getTimeAgo(timestamp: Long): String {
            val diff = System.currentTimeMillis() - timestamp
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            return when {
                days > 0 -> "$days day${if (days > 1) "s" else ""} ago"
                hours > 0 -> "$hours hour${if (hours > 1) "s" else ""} ago"
                minutes > 0 -> "$minutes minute${if (minutes > 1) "s" else ""} ago"
                else -> "Just now"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe, currentUserId, onEditClick, onDeleteClick)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }
}
