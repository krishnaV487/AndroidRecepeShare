package com.example.myapplication.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.UsersAdapter
import com.example.myapplication.databinding.ActivityCreateGroupBinding
import com.example.myapplication.db.Group
import com.example.myapplication.firestore.GroupRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateGroupBinding
    private lateinit var groupRepository: GroupRepository
    private val selectedMembers = mutableListOf<String>()
    private val firestore = FirebaseFirestore.getInstance()

    private lateinit var usersAdapter: UsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        groupRepository = GroupRepository(this)

        setupUserSearch()
        setupCreateGroupButton()
    }

    private fun setupUserSearch() {
        usersAdapter = UsersAdapter { userId, userName ->
            // Add user to selected members
            if (!selectedMembers.contains(userId)) {
                selectedMembers.add(userId)
                updateMembersList()
            } else {
                Toast.makeText(this, "$userName is already added!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.usersRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.usersRecyclerView.adapter = usersAdapter

        binding.userSearchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchUsers(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchUsers(newText ?: "")
                return true
            }
        })
    }

    private fun searchUsers(query: String) {
        lifecycleScope.launch {
            try {
                val result = firestore.collection("users")
                    .whereGreaterThanOrEqualTo("firstName", query)
                    .get()
                    .await()

                val users = result.documents.map {
                    val userId = it.id
                    val userName = it.getString("firstName") + " " + it.getString("lastName")
                    Pair(userId, userName)
                }

                usersAdapter.submitList(users)
            } catch (e: Exception) {
                Toast.makeText(this@CreateGroupActivity, "Failed to search users", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateMembersList() {
        val membersText = selectedMembers.joinToString(", ")
        binding.membersListTextView.text = if (membersText.isEmpty()) "None" else membersText
    }

    private fun setupCreateGroupButton() {
        binding.createGroupButton.setOnClickListener {
            val groupName = binding.groupNameEditText.text.toString()
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (groupName.isNotEmpty() && currentUser != null) {
                val groupId = UUID.randomUUID().toString()
                selectedMembers.add(currentUser.uid) // Ensure the creator is added

                val group = Group(
                    groupId = groupId,
                    groupName = groupName,
                    createdBy = currentUser.uid,
                    members = selectedMembers
                )

                lifecycleScope.launch {
                    try {
                        groupRepository.createGroup(group)
                        Toast.makeText(this@CreateGroupActivity, "Group created successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(this@CreateGroupActivity, "Failed to create group!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Group name cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
