package com.example.myapplication

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ItemRecipeBinding
import com.example.myapplication.db.Recipe

class RecipeAdapter : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

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
        fun bind(recipe: Recipe) {
            // Load Recipe Image or Placeholder
            val imageUrl = recipe.recipeImage.takeIf { it.isNotBlank() }
            Glide.with(binding.root.context)
                .load(imageUrl ?: R.drawable.placeholder_image)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(binding.recipeImage)

            // Set Title and Truncate Description
            binding.recipeTitle.text = recipe.title
            binding.recipeDescription.text = recipe.description.take(50) + "..."

            // Set Formatted Timestamp
            binding.recipeTimestamp.text = getTimeAgo(recipe.timestamp)
        }

        private fun getTimeAgo(timestamp: Long): String {
            val currentTime = System.currentTimeMillis()
            val diff = currentTime - timestamp

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
        Log.d("RecipeAdapter", "Binding recipe: ${recipe.title}")
        holder.bind(recipe)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }
}
