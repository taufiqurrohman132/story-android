package com.example.instogramapplication.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.instogramapplication.ui.main.MainActivity
import com.example.instogramapplication.ui.auth.login.LoginActivity
import com.example.instogramapplication.viewmodel.UserViewModelFactory

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val factory: UserViewModelFactory by lazy {
        UserViewModelFactory.getInstance(this)
    }
    private val viewModel: SplashViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // splash bawaan andorid 12+

        super.onCreate(savedInstanceState)

        viewModel.isLoggedIn.observe(this) { isLogin ->
            if (isLogin) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }
    }
}