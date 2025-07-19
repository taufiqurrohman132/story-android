package com.example.instogramapplication.ui.story.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instogramapplication.data.repository.UserRepository
import com.example.instogramapplication.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailStoryViewModel(
    private val repository: UserRepository
) : ViewModel() {
//    private val _detailStory = MutableStateFlow<Resource<Story>>(Resource.Loading())
//    val detailStory: StateFlow<Resource<Story>> = _detailStory
//
//    fun loadDetailStory(id: String){
//        viewModelScope.launch {
////            _detailStory.value = Resource.Loading()
//            val result = repository.getDetailStory(id)
//            _detailStory.value = result
//        }
//    }

}