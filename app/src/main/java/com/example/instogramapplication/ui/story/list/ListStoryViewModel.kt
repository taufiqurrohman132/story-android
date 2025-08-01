package com.example.instogramapplication.ui.story.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val refreshTrigger =
        MutableSharedFlow<Unit>()

    private val _eventFlow = Channel<String>()
    val eventFlow = _eventFlow.receiveAsFlow()


    @OptIn(ExperimentalCoroutinesApi::class)
    val storiesState = refreshTrigger
        .onStart { emit(Unit) } // auto-fetch saat pertama kali di-observe
        .flatMapLatest {
            flow {
                emit(Resource.Loading())
                delay(1000)
                val result = repository.getStories()// hanya bisa di konsum sekali
                result.collect { res ->
                    emit(res)
                    if (
                        res is Resource.Error ||
                        res is Resource.ErrorConnection ||
                        res is Resource.Empty
                    ) {
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

    fun refresh() {
        viewModelScope.launch {
            refreshTrigger.emit(Unit)
        }
    }


}