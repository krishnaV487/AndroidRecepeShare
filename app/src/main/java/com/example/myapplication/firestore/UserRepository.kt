package com.example.myapplication.firestore

import android.content.Context
import com.example.myapplication.db.AppDb
import com.example.myapplication.db.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class UserRepository(context: Context) {

    private val db: AppDb = AppDb.getInstance(context)
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Login User
    suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Register User
    suspend fun registerUser(firstName: String, lastName: String, email: String, password: String): Boolean {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: return false
            val user = User(userId, firstName, lastName, email)

            // Add to Firestore
            firestore.collection("users").document(userId).set(user).await()
            // Add to Room
            withContext(Dispatchers.IO) { db.userDao().insertUser(user) }
            true
        } catch (e: Exception) {
            false
        }
    }

    // Logout User
    fun logoutUser() {
        auth.signOut()
    }
}
