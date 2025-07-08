package com.example.instogramapplication.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.instogramapplication.data.di.Injection
import com.example.instogramapplication.data.repository.UserRepository
import com.example.instogramapplication.ui.auth.login.LoginViewModel
import com.example.instogramapplication.ui.auth.signup.SignUpViewModel
import com.example.instogramapplication.ui.story.detail.DetailStoryViewModel
import com.example.instogramapplication.ui.story.list.ListStoryViewModel

class UserViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {
    companion object{
        @Volatile
        private var instance: UserViewModelFactory? = null

        fun getInstance(context: Context): UserViewModelFactory =
            instance ?: synchronized(this){
                instance ?: UserViewModelFactory(Injection.provideRepository(context))
            }
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)){
            return SignUpViewModel(repository) as T
        }else if (modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(repository) as T
        }else if (modelClass.isAssignableFrom(ListStoryViewModel::class.java)){
            return ListStoryViewModel(repository) as T
        }else if (modelClass.isAssignableFrom(DetailStoryViewModel::class.java)){
            return DetailStoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknwon Viewmodel Class")
    }
}