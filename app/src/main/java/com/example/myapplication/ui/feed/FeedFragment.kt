package com.example.myapplication.ui.feed

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.RecipeAdapter
import com.example.myapplication.databinding.FragmentFeedBinding
import com.example.myapplication.db.Recipe
import com.example.myapplication.firestore.RecipeRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recipeRepository: RecipeRepository
    private val firestore = FirebaseFirestore.getInstance()
    private var allPublicRecipes: List<Recipe> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        recipeRepository = RecipeRepository(requireContext())

        setupRecyclerView()
        setupSearchBar()
        fetchPublicRecipes()

        return binding.root
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(
            currentUserId = "",
            onEditClick = {}, // Edit is not allowed in public feed
            onDeleteClick = {}, // Delete is not allowed in public feed
            onRecipeClick = { recipe ->
                // Navigate to RecipeDetailsActivity
                val intent = android.content.Intent(requireContext(), com.example.myapplication.ui.activity.RecipeDetailsActivity::class.java)
                intent.putExtra("recipeId", recipe.recipeId)
                startActivity(intent)
            }
        )

        binding.recipesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recipeAdapter
        }
    }

    private fun setupSearchBar() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                filterRecipes(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchPublicRecipes() {
        lifecycleScope.launch {
            try {
                val snapshot = firestore.collection("recipes")
                    .whereEqualTo("isPublic", true)
                    .get()
                    .await()

                allPublicRecipes = snapshot.toObjects(Recipe::class.java)
                recipeAdapter.submitList(allPublicRecipes)

                if (allPublicRecipes.isEmpty()) {
                    Toast.makeText(requireContext(), "No public recipes found.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Failed to load public recipes.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filterRecipes(query: String) {
        val filteredList = if (query.isEmpty()) {
            allPublicRecipes
        } else {
            allPublicRecipes.filter { it.title.contains(query, ignoreCase = true) }
        }
        recipeAdapter.submitList(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
