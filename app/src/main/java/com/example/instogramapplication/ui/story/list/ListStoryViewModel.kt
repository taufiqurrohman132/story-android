package com.example.instogramapplication.ui.story.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.instogramapplication.data.local.entity.StoryEntity
import com.example.instogramapplication.data.repository.UserRepository

class ListStoryViewModel(
    private val repository: UserRepository
) : ViewModel() {
    val myStory: LiveData<StoryEntity?> = liveData {
        val username = repository.getUserName()
        emitSource(repository.getLatestMyStory(username))
    }
    val storiesY: LiveData<PagingData<StoryEntity>> =
        repository.getStories().cachedIn(viewModelScope)

}

