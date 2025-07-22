package com.example.instogramapplication.ui.story.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instogramapplication.data.repository.UserRepository
import com.example.instogramapplication.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListStoryViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private var _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName

    private val refreshTrigger =
        MutableSharedFlow<Unit>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val storiesState = refreshTrigger
        .onStart { emit(Unit) } // auto-fetch saat pertama kali di-observe
        .flatMapLatest {
            repository.getStories()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(),
            Resource.Loading()
        )

    var isCollaps = false

    init {
        viewModelScope.launch {
            val name = repository.getUserName()
            _userName.value = name
        }
    }

    fun refresh(){
        viewModelScope.launch {
            refreshTrigger.emit(Unit)
        }
    }
}