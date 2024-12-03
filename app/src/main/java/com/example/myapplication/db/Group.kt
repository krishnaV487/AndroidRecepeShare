package com.example.myapplication.db
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class Group(
    @PrimaryKey val groupId: String,
    val groupName: String,
    val createdBy: String, // userId of the creator
    val members: List<String> // List of userIds
)
