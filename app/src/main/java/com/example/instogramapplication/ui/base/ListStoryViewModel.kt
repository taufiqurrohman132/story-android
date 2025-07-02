package com.example.instogramapplication.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instogramapplication.data.repository.UserRepository
import com.example.instogramapplication.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListStoryViewModel(
    repository: UserRepository
) : ViewModel() {

    private val refreshTrigger =
        MutableSharedFlow<Unit>()

//    val storiesState = repository.getStories()
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Resource.Loading())

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

    fun refresh(){
        viewModelScope.launch {
            refreshTrigger.emit(Unit)
        }
    }
}