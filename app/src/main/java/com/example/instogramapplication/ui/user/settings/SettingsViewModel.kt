package com.example.instogramapplication.ui.user.settings

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instogramapplication.data.repository.UserRepository
import com.yariksoffice.lingver.Lingver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: UserRepository
) : ViewModel(){
    suspend fun getCurrentLang(): String {
        return repository.getCurrentLanguage()
    }

    fun setLanguage(context: Context, langCode: String, onComplite: () -> Unit) {
        Lingver.getInstance().setLocale(context, langCode)
        // save
        viewModelScope.launch {
            repository.setLanguage(langCode)
            onComplite()
        }
    }

    fun logOut(){
        viewModelScope.launch {
            repository.userLogout()
        }
    }
}