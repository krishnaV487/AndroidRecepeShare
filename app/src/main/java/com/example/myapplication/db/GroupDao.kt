package com.example.myapplication.db
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GroupDao {
    @Insert
    suspend fun insertGroup(group: Group)

    @Query("SELECT * FROM groups WHERE groupId = :id")
    suspend fun getGroupById(id: String): Group?

    @Query("DELETE FROM groups")
    suspend fun deleteAllGroups()
}
