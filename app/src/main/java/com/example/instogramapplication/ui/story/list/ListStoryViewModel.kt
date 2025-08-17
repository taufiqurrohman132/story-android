package com.example.instogramapplication.ui.story.list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.log
import androidx.paging.map
import com.example.instogramapplication.data.local.entity.StoryEntity
import com.example.instogramapplication.data.remote.model.StoryItem
import com.example.instogramapplication.data.repository.UserRepository
import com.example.instogramapplication.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
class ListStoryViewModel(
    private val repository: UserRepository
) : ViewModel() {

//    private val refreshTrigger =
//        MutableSharedFlow<Unit>()
//
//    private val _eventFlow = Channel<String>()
//    val eventFlow = _eventFlow.receiveAsFlow()

    private val _currentUserName = MutableLiveData<String>()
    val myStory = MutableLiveData<StoryItem>() // untuk story khusus user

    init {
        viewModelScope.launch {
            _currentUserName.value = repository.getUserName()
        }
    }

    val story: LiveData<PagingData<StoryEntity>> =
        repository.getStories().cachedIn(viewModelScope)
//        _currentUserName.switchMap { userName ->
//            repository.getStories()
//                .map { pagingData ->
//                    // cari story user
//                    val tempList = mutableListOf<StoryItem>()
//
//                    pagingData.filter { story ->
//                        Log.d(TAG, "paging data story = : $story")
//                        tempList.add(story)
//                        true // tetep masukan semua story
//                    }
//
//                    // ambil story terbaru user
//                    val latestMyStory = tempList
//                        .filter {
//                            Log.d(TAG, "name = ${it.name} - username = $userName ")
//                            it.name == userName
//                        }
//                        .maxByOrNull { it.createdAt ?: "" }
//                    Log.d(TAG, "latest my story = : $latestMyStory")
//
//                    myStory.postValue(latestMyStory)
//                    pagingData
//
//                }.cachedIn(viewModelScope)
//        }


//    @OptIn(ExperimentalCoroutinesApi::class)
//    val storiesState = refreshTrigger
//        .onStart { emit(Unit) } // auto-fetch saat pertama kali di-observe
//        .flatMapLatest {
//            flow {
//                emit(Resource.Loading())
//                delay(1000)
//                val result = repository.getStories()// hanya bisa di konsum sekali
//                result.collect { res ->
//                    emit(res)
//                    if (
//                        res is Resource.Error ||
//                        res is Resource.ErrorConnection ||
//                        res is Resource.Empty
//                    ) {
//                        res.message?.let {
//                            _eventFlow.send(res.message)
//                        }
//                    }
//                }
//            }
//        }
//        .distinctUntilChanged()
//        .stateIn(
//            viewModelScope,
//            SharingStarted.WhileSubscribed(0),
//            Resource.Loading()
//        )

    var isCollaps = false

//    fun refresh() {
//        viewModelScope.launch {
//            refreshTrigger.emit(Unit)
//        }
//    }

    companion object{
        private val TAG = ListStoryViewModel::class.java.simpleName
    }
}

