package com.example.instogramapplication.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.instogramapplication.SplashViewModel
import com.example.instogramapplication.data.di.Injection
import com.example.instogramapplication.data.repository.UserRepository
import com.example.instogramapplication.ui.auth.login.LoginViewModel
import com.example.instogramapplication.ui.auth.signup.SignUpViewModel
import com.example.instogramapplication.ui.story.detail.DetailStoryViewModel
import com.example.instogramapplication.ui.story.list.ListStoryViewModel
import com.example.instogramapplication.ui.story.post.EditViewModel
import com.example.instogramapplication.ui.user.settings.SettingsViewModel

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
        return when(modelClass){
            SignUpViewModel::class.java ->
                SignUpViewModel(repository)
            LoginViewModel::class.java ->
                LoginViewModel(repository)
            ListStoryViewModel::class.java ->
                ListStoryViewModel(repository)
            DetailStoryViewModel::class.java ->
                DetailStoryViewModel(repository)
            EditViewModel::class.java ->
                EditViewModel(repository)
            SplashViewModel::class.java ->
                SplashViewModel(repository)
            SettingsViewModel::class.java ->
                SettingsViewModel(repository)
            else ->
                throw IllegalArgumentException("Unknwon Viewmodel Class")
        } as T
    }
}