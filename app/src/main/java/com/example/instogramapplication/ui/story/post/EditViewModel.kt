package com.example.instogramapplication.ui.story.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instogramapplication.data.repository.UserRepository
import com.example.instogramapplication.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class EditViewModel(
    private val repository: UserRepository
) : ViewModel() {
    private var _uploadState = MutableStateFlow<Resource<String>>(Resource.Empty())
    val uploadState: StateFlow<Resource<String>> = _uploadState

    private val _isLocationSelected = MutableLiveData(false)
    val isLocationSelected: LiveData<Boolean> get() = _isLocationSelected

    fun toggleLocationSelected() {
        _isLocationSelected.value = !(_isLocationSelected.value ?: false)
    }

    fun uploadStory(imageFile: File, desc: String, lat: String?, lon: String?) {
        viewModelScope.launch {
            _uploadState.value = Resource.Loading()
            val result = repository.uploadStory(imageFile, desc, lat, lon)
            _uploadState.value = result
        }
    }
}