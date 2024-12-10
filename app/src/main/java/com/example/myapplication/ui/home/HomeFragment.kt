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
        // Initialize RecyclerView
        recipeAdapter = RecipeAdapter()
        binding.recipesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recipeAdapter
        }

        // Set Login/Logout Button
        updateLoginLogoutButton()

        binding.loginLogoutButton.setOnClickListener {
            if (auth.currentUser == null) {
                // Navigate to LoginActivity
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            } else {
                // Logout
                auth.signOut()
                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
                updateLoginLogoutButton()
                recipeAdapter.submitList(emptyList()) // Clear recipes
                binding.helloText.text = "Hello! Log in to create!" // Reset text
            }
        }

        // Update Hello Text depending on user status
        updateHelloText()

        // Create Recipe Button
        binding.createRecipeButton.setOnClickListener {
            if (auth.currentUser != null) {
                // Navigate to CreateRecipeActivity
                startActivity(Intent(requireContext(), CreateRecipeActivity::class.java))
            } else {
                Toast.makeText(requireContext(), "Please log in to create a recipe", Toast.LENGTH_SHORT).show()
            }
        }


        // Fetch Recipes if Logged In
        fetchUserRecipes()
    }

    @SuppressLint("SetTextI18n")
    private fun updateHelloText() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            binding.helloText.text = "Hello! Log in to create!"
        } else {
            val userId = currentUser.uid
            // Fetch the username from Firestore or Room
            lifecycleScope.launch {
                val db = AppDb.getInstance(requireContext())
                val localUser = db.userDao().getUserById(userId)

                // If local user exists, show the name; else, fetch from Firestore
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
        if (currentUser == null) {
            binding.loginLogoutButton.text = "Login"
        } else {
            binding.loginLogoutButton.text = "Logout"
        }
    }

    private fun fetchUserRecipes() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            lifecycleScope.launch {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

                // Fetch recipes from Firestore and cache in Room
                recipeRepository.fetchRecipesFromFirestore(userId)

                // Load locally stored recipes for offline access
                val recipes = recipeRepository.getLocalRecipes(userId)
                recipeAdapter.submitList(recipes)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
