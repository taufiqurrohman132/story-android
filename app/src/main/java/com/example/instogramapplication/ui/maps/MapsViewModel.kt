package com.example.instogramapplication.ui.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val _storiesState = MutableLiveData<Resource<List<StoryItem>>>()
    val storiesState: LiveData<Resource<List<StoryItem>>> = _storiesState

    // notif
    private val _eventChannel = Channel<String>()
    val eventFlow = _eventChannel.receiveAsFlow()

    fun loadStories(location: Int) {
        Log.d(TAG, "loadStories: location = $location")
        viewModelScope.launch {
            _storiesState.postValue(Resource.Loading())
//            try {
//                repository.getStories(location).collect { res ->
//                    _storiesState.postValue(res)
//                    if (
//                        res is Resource.Error ||
//                        res is Resource.ErrorConnection ||
//                        res is Resource.Empty
//                    ) {
//                        res.message?.let { msg ->
//                            _eventChannel.send(msg) // kirim notif sekali pakai
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                _eventChannel.send(e.message ?: "Unknown error")
//            }
        }
    }

    companion object {
        private val TAG = MapsViewModel::class.java.simpleName
    }
}
