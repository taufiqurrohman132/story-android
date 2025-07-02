package com.example.instogramapplication.ui.auth.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instogramapplication.data.repository.UserRepository
import com.example.instogramapplication.utils.Resource
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val repository: UserRepository
) : ViewModel() {
    // proses yang tidak sering berulang
    private val _registerResult = MutableLiveData<Resource<String>>()
    val registerResult: LiveData<Resource<String>> = _registerResult

    fun register(name: String, email: String, password: String){
        viewModelScope.launch {
            _registerResult.value = Resource.Loading()
            val result = repository.userRegister(name, email, password)
            _registerResult.value = result
        }
    }
}