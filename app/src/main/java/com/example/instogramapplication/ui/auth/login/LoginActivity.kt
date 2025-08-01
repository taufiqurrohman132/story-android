package com.example.instogramapplication.ui.auth.login

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.instogramapplication.ui.main.MainActivity
import com.example.instogramapplication.R
import com.example.instogramapplication.databinding.ActivityLoginBinding
import com.example.instogramapplication.ui.auth.signup.SignUpActivity
import com.example.instogramapplication.utils.DialogUtils
import com.example.instogramapplication.utils.DialogUtils.showToast
import com.example.instogramapplication.utils.Resource
import com.example.instogramapplication.utils.ValidationUtils
import com.example.instogramapplication.utils.constants.DialogType
import com.example.instogramapplication.viewmodel.UserViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val factory: UserViewModelFactory by lazy {
        UserViewModelFactory.getInstance(this)
    }
    private val viewModel: LoginViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setupListener()
        observer()
    }

    private fun initView() {
        // setup
        binding.apply {
            loginInlayEmail.apply {
                setTextError(context.getString(R.string.error_invalid_email))
                isSucces = {
                    ValidationUtils.isEmailValid(it)
                }
            }
            loginInlayPass.apply {
                setTextError(context.getString(R.string.error_password_too_short))
                isSucces = {
                    !(!it.isNullOrBlank() && it.length < 8)
                }
            }
        }

        // setup animation
        binding.formLogin.post {
            ObjectAnimator.ofFloat(binding.formLogin, View.TRANSLATION_Y, 600f, 0f).apply {
                duration = 1000
                interpolator = DecelerateInterpolator()
                start()
            }
        }
    }

    private fun setupListener() {
        binding.apply {
            loginBtnLogin.setOnClickListener { handlerLogin() }
            loginTvDaftar.setOnClickListener { navigateToRegister() }
        }
    }

    private fun handlerLogin() {
        val email = binding.loginEdtEmail.text.toString().trim()
        val pass = binding.loginEdtPass.text.toString().trim()
        doLogin(email, pass)
    }

    private fun doLogin(email: String, pass: String) {
        viewModel.login(email, pass)
    }

    private fun observer() {
        viewModel.loginResult.observe(this) { story ->
            when (story) {
                is Resource.Loading -> {
                    // tampilkan progress bar
                    showLoading(true)
                }

                is Resource.Success -> {
                    // pindah ke LoginActivity atau Home
                    showLoading(false)
                    showSuccess()
                }

                is Resource.Error -> {
                    // tampilkan error
                    showLoading(false)
                    story.message?.let {
                        showError(story.message)
                    }
                }

                is Resource.ErrorConnection -> {
                    // tampilkan error
                    showLoading(false)
                    showToast(this.getString(R.string.error_koneksi), this)
                }

                else -> {
                    showLoading(false)
                }
            }
        }
    }

    private fun navigateToRegister() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            dimOverlay.isVisible = isLoading
            loading.isVisible = isLoading
        }
    }

    private fun showError(desc: String) {
        DialogUtils.stateDialog(
            this,
            DialogType.ERROR,
            this.getString(R.string.popup_error_title),
            desc,
            this.getString(R.string.popup_error_btn)
        ) {
            it.dismiss()
            binding.dimOverlay.visibility = View.INVISIBLE

        }
    }

    private fun showSuccess() {
        DialogUtils.stateDialog(
            this,
            DialogType.SUCCESS,
            this.getString(R.string.popup_success_title),
            this.getString(R.string.popup_success_desc),
            this.getString(R.string.popup_success_btn),
        ) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()

            it.dismiss()
            binding.dimOverlay.visibility = View.INVISIBLE
        }
    }

    companion object {
        val TAG = LoginActivity::class.java.simpleName
    }
}