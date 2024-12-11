package com.example.myapplication.firestore

import android.content.Context
import android.util.Log
import com.example.myapplication.db.AppDb
import com.example.myapplication.db.Group
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class GroupRepository(context: Context) {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getGroupsByUser(userId: String): List<Group> = withContext(Dispatchers.IO) {
        try {
            Log.e("FIRESTOREREPO",userId)
            val snapshot = firestore.collection("groups")
                .whereArrayContains("members", userId)
                .get()
                .await()

            Log.e("QUESRYRES", snapshot.toObjects(Group::class.java).toString())
            return@withContext snapshot.toObjects(Group::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    suspend fun createGroup(group: Group) {
        try {
            firestore.collection("groups").document(group.groupId).set(group).await()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

}
