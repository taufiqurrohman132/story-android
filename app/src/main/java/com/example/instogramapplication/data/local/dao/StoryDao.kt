package com.example.instogramapplication.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.instogramapplication.data.local.entity.StoryEntity

@Dao
interface StoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<StoryEntity>)

    @Query("Select * from story ORDER BY createdAt DESC")
    fun getAllStory(): PagingSource<Int, StoryEntity>

    @Query("Delete from story")
    suspend fun deleteAll()

    @Query("SELECT * FROM story WHERE name = :username ORDER BY createdAt DESC LIMIT 1")
    fun getLatestMyStory(username: String): LiveData<StoryEntity?>

    @Query("SELECT * FROM story WHERE lat IS NOT NULL AND lon IS NOT NULL")
    fun getStoriesForMap(): LiveData<List<StoryEntity>>
}