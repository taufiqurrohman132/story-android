package com.example.instogramapplication

import com.example.instogramapplication.data.local.entity.StoryEntity

object DataDummy {
    fun generateDummyStoryResponse(): List<StoryEntity>{
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100){
            val story = StoryEntity(
                id = i.toString(),
                photoUrl = "https://picsum.photos/200/30$i",
                createdAt = "2025-08-19T10:15:00Z",
                name = "Budi $i",
                description = "Belajar Android Kotlin, seru banget! part $i",
                lon = null,      // tanpa lokasi
                lat = null
            )
            items.add(story)
        }
        return items
    }
}