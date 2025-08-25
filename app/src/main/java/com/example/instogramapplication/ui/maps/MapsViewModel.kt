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

    // notif
//    private val _eventChannel = Channel<String>()
//    val eventFlow = _eventChannel.receiveAsFlow()

//    private val locationParam = MutableLiveData<Int>()
//    val location: LiveData<PagingData<StoryEntity>> =
//        locationParam.switchMap { location ->
//            repository.getStories(location)
//                .cachedIn(viewModelScope)
//        }
//
//    fun loadStory(location: Int){
//        locationParam.value = location
//    }
//    val pagingStories: LiveData<PagingData<StoryEntity>> =
//        repository.getStories(location = 1)


//    val storiesForMap: LiveData<List<StoryEntity>> =
//        repository.getStoriesForMap()
//
    fun loadstoriesForMap() {
        val source = repository.getStoriesForMap()
        _storiesForMap.addSource(source) { story ->
            Log.d(TAG, "loadstoriesForMap: loas story viewmodel is running")
            if (story.isNullOrEmpty()){
                Log.d(TAG, "loadstoriesForMap: load from api")
                viewModelScope.launch {
                    repository.fetchStoriesFromApi(1)
                }
                _storiesForMap.value = emptyList()
            }else {
                _storiesForMap.value = story
            }
            _storiesForMap.removeSource(source)// agar tidak doble
        }
    }

    companion object {
        private val TAG = MapsViewModel::class.java.simpleName
    }
}
