package com.example.myapplication.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.ui.activity.LoginActivity
import com.example.myapplication.RecipeAdapter
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.firestore.RecipeRepository
import com.example.myapplication.db.AppDb
import com.example.myapplication.ui.activity.CreateRecipeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var recipeRepository: RecipeRepository
    private lateinit var recipeAdapter: RecipeAdapter

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = FirebaseAuth.getInstance()
        recipeRepository = RecipeRepository(requireContext())

        setupUI()
        return root
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        val currentUserId = auth.currentUser?.uid ?: ""

        // Initialize RecipeAdapter with callbacks
        recipeAdapter = RecipeAdapter(
            currentUserId = currentUserId,
            onEditClick = { recipe ->
                val intent = Intent(requireContext(), CreateRecipeActivity::class.java)
                intent.putExtra("recipeId", recipe.recipeId)
                startActivity(intent)
            },
            onDeleteClick = { recipe ->
                deleteRecipe(recipe.recipeId)
            }
        )

        // Setup RecyclerView
        binding.recipesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recipeAdapter
        }

        // Set Login/Logout Button
        updateLoginLogoutButton()

        binding.loginLogoutButton.setOnClickListener {
            if (auth.currentUser == null) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            } else {
                auth.signOut()
                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
                updateLoginLogoutButton()
                recipeAdapter.submitList(emptyList()) // Clear recipes
                binding.helloText.text = "Hello! Log in to create!"
            }
        }

        // Update Hello Text
        updateHelloText()

        // Create Recipe Button
        binding.createRecipeButton.setOnClickListener {
            if (auth.currentUser != null) {
                startActivity(Intent(requireContext(), CreateRecipeActivity::class.java))
            } else {
                Toast.makeText(requireContext(), "Please log in to create a recipe", Toast.LENGTH_SHORT).show()
            }
        }

        // Fetch Recipes
        fetchRecipes()
    }

    private fun fetchRecipes() {
        val currentUserId = auth.currentUser?.uid ?: return
        lifecycleScope.launch {
            try {
                recipeRepository.fetchRecipesFromFirestore(currentUserId)
                val recipes = recipeRepository.getLocalRecipes(currentUserId)
                recipeAdapter.submitList(recipes)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to fetch recipes", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun deleteRecipe(recipeId: String) {
        lifecycleScope.launch {
            try {
                recipeRepository.deleteRecipe(recipeId)
                Toast.makeText(requireContext(), "Recipe deleted successfully", Toast.LENGTH_SHORT).show()
                fetchRecipes() // Refresh recipes
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to delete recipe", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateHelloText() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            binding.helloText.text = "Hello! Log in to create!"
        } else {
            val userId = currentUser.uid
            lifecycleScope.launch {
                val db = AppDb.getInstance(requireContext())
                val localUser = db.userDao().getUserById(userId)

                if (localUser != null) {
                    binding.helloText.text = "Hello ${localUser.firstName}!"
                } else {
                    fetchUsernameFromFirestore(userId)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fetchUsernameFromFirestore(userId: String) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val firstName = document.getString("firstName") ?: "User"
                binding.helloText.text = "Hello $firstName!"
            }
            .addOnFailureListener {
                binding.helloText.text = "Hello User!"
            }
    }

    @SuppressLint("SetTextI18n")
    private fun updateLoginLogoutButton() {
        val currentUser = auth.currentUser
        binding.loginLogoutButton.text = if (currentUser == null) "Login" else "Logout"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
