package com.example.instogramapplication.ui.maps

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.instogramapplication.data.local.entity.StoryEntity
import com.example.instogramapplication.data.remote.model.StoryItem
import com.example.instogramapplication.data.repository.UserRepository
import com.example.instogramapplication.utils.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class MapsViewModel(
    private val repository: UserRepository
) : ViewModel() {

    // state untuk daftar cerita
    private val _storiesForMap = MediatorLiveData<List<StoryEntity>>()
    val storiesForMap: LiveData<List<StoryEntity>> get() =  _storiesForMap

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
