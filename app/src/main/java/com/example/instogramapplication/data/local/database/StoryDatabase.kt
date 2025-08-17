package com.example.instogramapplication.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.instogramapplication.data.local.dao.RemoteKeysDao
import com.example.instogramapplication.data.local.dao.StoryDao
import com.example.instogramapplication.data.local.entity.RemoteKeys
import com.example.instogramapplication.data.local.entity.StoryEntity


@Database(
    entities = [StoryEntity::class, RemoteKeys::class],
    version = 2,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object{
        @Volatile
        private var INSTANCE: StoryDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): StoryDatabase =
            INSTANCE ?: synchronized(this){
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java,
                    "story_database"
                )
                    .build()
                    .also { INSTANCE ?: it }
            }
    }
}