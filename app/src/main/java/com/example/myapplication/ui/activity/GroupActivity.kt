package com.example.myapplication.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.MembersAdapter
import com.example.myapplication.RecipeAdapter
import com.example.myapplication.databinding.ActivityGroupBinding
import com.example.myapplication.db.Recipe
import com.example.myapplication.db.User
import com.example.myapplication.firestore.GroupRepository
import com.example.myapplication.firestore.RecipeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class GroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupBinding
    private lateinit var recipeRepository: RecipeRepository
    private lateinit var groupRepository: GroupRepository
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var membersAdapter: MembersAdapter

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var groupId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        groupId = intent.getStringExtra("groupId") // Fetch Group ID from Intent

        recipeRepository = RecipeRepository(this)
        groupRepository = GroupRepository(this)

        setupAdapters()
        fetchGroupData()
    }

    private fun setupAdapters() {
        // Recipe Adapter
        recipeAdapter = RecipeAdapter(
            currentUserId = auth.currentUser?.uid ?: "",
            onEditClick = {}, // No editing in this context
            onDeleteClick = {}
        )
        binding.recipesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@GroupActivity)
            adapter = recipeAdapter
        }

        // Members Adapter
        membersAdapter = MembersAdapter()
        binding.membersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@GroupActivity)
            adapter = membersAdapter
        }
    }

    private fun fetchGroupData() {
        if (groupId == null) {
            Toast.makeText(this, "Group not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                // Fetch recipes with this group's ID
                val recipes = fetchGroupRecipes(groupId!!)
                recipeAdapter.submitList(recipes)

                // Show 'No recipe shared' message if needed
                binding.noRecipesText.visibility =
                    if (recipes.isEmpty()) View.VISIBLE else View.GONE

                // Fetch group details and members
                val groupSnapshot = firestore.collection("groups").document(groupId!!).get().await()
                val memberIds = groupSnapshot.get("members") as? List<String> ?: emptyList()

                fetchMemberDetails(memberIds)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@GroupActivity, "Error loading group data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun fetchGroupRecipes(groupId: String): List<Recipe> {
        return try {
            val snapshot = firestore.collection("recipes")
                .whereArrayContains("groupIdList", groupId)
                .get()
                .await()
            snapshot.toObjects(Recipe::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private suspend fun fetchMemberDetails(memberIds: List<String>) {
        val members = mutableListOf<Member>()

        for (userId in memberIds) {
            try {
                val userSnapshot = firestore.collection("users").document(userId).get().await()
                val user = userSnapshot.toObject(User::class.java)
                user?.let {
                    members.add(Member(it.userId, "${it.firstName} ${it.lastName}"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Update the existing adapter's list
        membersAdapter.submitList(members)
    }
}

// Data class for displaying members
data class Member(val userId: String, val name: String)
