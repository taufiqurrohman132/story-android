package com.example.instogramapplication.ui.story.list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instogramapplication.data.remote.model.ListStoryItem
import com.example.instogramapplication.data.repository.UserRepository
import com.example.instogramapplication.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListStoryViewModel(
    private val repository: UserRepository
) : ViewModel() {

//    private var _userName = MutableLiveData<String>()
//    val userName: LiveData<String> get() = _userName

    private val refreshTrigger =
        MutableSharedFlow<Unit>()

    private val _eventFlow = Channel<String>()
    val eventFlow = _eventFlow.receiveAsFlow()


    @OptIn(ExperimentalCoroutinesApi::class)
    val storiesState = refreshTrigger
        .onStart { emit(Unit) } // auto-fetch saat pertama kali di-observe
        .flatMapLatest {
            flow {
                Log.d(TAG, "stories isrunning: ")
                emit(Resource.Loading())
                delay(1000)
                val result = repository.getStories()// hanya bisa di konsum sekali
                result.collect{ res ->
                    emit(res)
                    Log.d(TAG, "emit: $res")
                    if (
                        res is Resource.Error ||
                        res is Resource.ErrorConnection ||
                        res is Resource.Empty
                    ){
                        res.message?.let {
                            _eventFlow.send(res.message)
                        }
                    }
                }
            }
        }
        .distinctUntilChanged()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(0),
            Resource.Loading()
        )

    var isCollaps = false

//    init {
//        viewModelScope.launch {
//            val name = repository.getUserName()
//            _userName.value = name
//        }
//    }

    fun refresh(){
        viewModelScope.launch {
            refreshTrigger.emit(Unit)
        }
    }
    
    companion object{
        private val TAG = ListStoryViewModel::class.java.simpleName
    }
}