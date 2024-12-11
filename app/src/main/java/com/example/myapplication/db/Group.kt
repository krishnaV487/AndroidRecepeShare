package com.example.myapplication.db
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class Group(
    @PrimaryKey val groupId: String = "",       // Default value: empty string
    val groupName: String = "",                 // Default value: empty string
    val createdBy: String = "",                 // Default value: empty string
    val members: List<String> = emptyList()     // Default value: empty list
)