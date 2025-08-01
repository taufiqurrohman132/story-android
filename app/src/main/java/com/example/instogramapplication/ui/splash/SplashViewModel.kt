package com.example.instogramapplication.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.instogramapplication.data.repository.UserRepository

class SplashViewModel(
    repository: UserRepository
) : ViewModel() {
    val isLoggedIn: LiveData<Boolean> = repository.isLoggedIn().asLiveData()
}