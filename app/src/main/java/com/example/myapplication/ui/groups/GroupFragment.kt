package com.example.myapplication.ui.groups

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.firestore.GroupRepository
import com.example.myapplication.firestore.RecipeRepository
import com.example.myapplication.ui.activity.CreateGroupActivity
import com.example.myapplication.GroupsAdapter
import com.example.myapplication.RecipeAdapter
import com.example.myapplication.databinding.FragmentGroupBinding
import com.example.myapplication.ui.activity.CreateRecipeActivity
import com.example.myapplication.ui.activity.RecipeDetailsActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class GroupsFragment : Fragment() {

    private var _binding: FragmentGroupBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var groupRepository: GroupRepository
    private lateinit var recipeRepository: RecipeRepository
    private lateinit var groupsAdapter: GroupsAdapter
    private lateinit var recipesAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        groupRepository = GroupRepository(requireContext())
        recipeRepository = RecipeRepository(requireContext())

        setupUI()
        return binding.root
    }

    private fun setupUI() {
        // Initialize Adapters
        groupsAdapter = GroupsAdapter()
        recipesAdapter = RecipeAdapter(
            currentUserId = auth.currentUser?.uid ?: "", // Pass the current user's ID
            onEditClick = { recipe ->
                // Handle edit click: Navigate to CreateRecipeActivity in "edit" mode
                val intent = Intent(requireContext(), CreateRecipeActivity::class.java)
                intent.putExtra("recipeId", recipe.recipeId)
                startActivity(intent)
            },
            onDeleteClick = { recipe ->
                // Handle delete click: Delete recipe and refresh the list
                lifecycleScope.launch {
                    try {
                        recipeRepository.deleteRecipe(recipe.recipeId)
                        fetchGroupsAndRecipes() // Refresh list after deletion
                        Toast.makeText(requireContext(), "Recipe deleted", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "Failed to delete recipe", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onRecipeClick = { recipe ->
                // Navigate to RecipeDetailsActivity
                val intent = Intent(requireContext(), RecipeDetailsActivity::class.java)
                intent.putExtra("recipeId", recipe.recipeId)
                startActivity(intent)
            }
        )

        binding.groupsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = groupsAdapter
        }

        binding.recipesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recipesAdapter
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Show login message
            binding.loginMessage.visibility = View.VISIBLE
            binding.createGroupButton.visibility = View.GONE
            binding.groupsRecyclerView.visibility = View.GONE
            binding.recipesLabel.visibility = View.GONE
            binding.recipesRecyclerView.visibility = View.GONE
        } else {
            // Show Create Group button and sections
            binding.loginMessage.visibility = View.GONE
            binding.createGroupButton.visibility = View.VISIBLE
            binding.groupsRecyclerView.visibility = View.VISIBLE
            binding.recipesLabel.visibility = View.VISIBLE
            binding.recipesRecyclerView.visibility = View.VISIBLE

            // Set Create Group Button Action
            binding.createGroupButton.setOnClickListener {
                startActivity(Intent(requireContext(), CreateGroupActivity::class.java))
            }

            fetchGroupsAndRecipes()
        }
    }

    private fun fetchGroupsAndRecipes() {
        val userId = auth.currentUser?.uid ?: return

        lifecycleScope.launch {
            try {
                // Fetch Groups
                val groups = groupRepository.getGroupsByUser(userId)
                groupsAdapter.submitList(groups)

                // Fetch Recipes
                val recipes = recipeRepository.getRecipesSharedWithUser(userId)
                recipesAdapter.submitList(recipes)

                if (groups.isEmpty()) {
                    Toast.makeText(requireContext(), "No groups found!", Toast.LENGTH_SHORT).show()
                }
                if (recipes.isEmpty()) {
                    Toast.makeText(requireContext(), "No shared recipes found!", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
