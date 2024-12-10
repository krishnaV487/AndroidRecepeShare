package com.example.myapplication.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.MainActivity
import com.example.myapplication.databinding.ActivityRegisterBinding
import com.example.myapplication.db.AppDb
import com.example.myapplication.db.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Handle Register Button Click
        binding.registerButton.setOnClickListener {
            val firstName = binding.firstNameEditText.text.toString().trim()
            val lastName = binding.lastNameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(firstName, lastName, email, password)
            } else {
                binding.errorTextView.text = "All fields are required"
            }
        }
    }

    private fun registerUser(firstName: String, lastName: String, email: String, password: String) {
        // Show ProgressBar
        showLoading(true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid ?: return@addOnSuccessListener
                val newUser = User(userId, firstName, lastName, email)

                // Add to Firestore
                firestore.collection("users").document(userId).set(newUser)
                    .addOnSuccessListener {
                        // Save locally in Room
                        lifecycleScope.launch(Dispatchers.IO) {
                            val db = AppDb.getInstance(this@RegisterActivity)
                            db.userDao().insertUser(newUser)

                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@RegisterActivity, "Registration Successful!", Toast.LENGTH_SHORT).show()
                                clearInputFields()
                                navigateToHome()
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        showError("Failed to add user: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                showError("Registration failed: ${e.message}")
            }
            .addOnCompleteListener {
                // Hide ProgressBar
                showLoading(false)
            }
    }

    private fun clearInputFields() {
        binding.firstNameEditText.text.clear()
        binding.lastNameEditText.text.clear()
        binding.emailEditText.text.clear()
        binding.passwordEditText.text.clear()
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("navigateToHome", true) // Flag for navigation
        startActivity(intent)
        finish()
    }

    private fun showError(message: String) {
        binding.errorTextView.text = message
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.registerButton.isEnabled = !isLoading
    }
}
