package com.example.instogramapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.instogramapplication.data.repository.UserRepository
import kotlinx.coroutines.flow.StateFlow

class SplashViewModel(
    repository: UserRepository
) : ViewModel(){
    val isLoggedIn: LiveData<Boolean>  = repository.isLoggedIn().asLiveData()
}