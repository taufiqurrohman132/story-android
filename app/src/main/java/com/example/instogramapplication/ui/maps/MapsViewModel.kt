package com.example.instogramapplication.ui.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instogramapplication.data.local.entity.StoryEntity
import com.example.instogramapplication.data.repository.UserRepository
import kotlinx.coroutines.launch


class MapsViewModel(
    private val repository: UserRepository
) : ViewModel() {

    // state untuk daftar cerita
    private val _storiesForMap = MediatorLiveData<List<StoryEntity>>()
    val storiesForMap: LiveData<List<StoryEntity>> get() = _storiesForMap

    fun loadstoriesForMap() {
        val source = repository.getStoriesForMap() // local
        _storiesForMap.addSource(source) { story ->
            Log.d(TAG, "loadstoriesForMap: loas story viewmodel is running")
            Log.d(TAG, "loadstoriesForMap: load from api size ${story.size}")
            viewModelScope.launch {
                repository.fetchStoriesFromApi(1)
            }
            _storiesForMap.value = story

            _storiesForMap.removeSource(source)// agar tidak doble
        }
    }

    companion object {
        private val TAG = MapsViewModel::class.java.simpleName
    }
}
