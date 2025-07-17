package com.example.instogramapplication.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.instogramapplication.MainActivity
import com.example.instogramapplication.ui.story.list.ListStoryFragment
import com.example.instogramapplication.databinding.ActivityLoginBinding
import com.example.instogramapplication.ui.auth.signup.SignUpActivity
import com.example.instogramapplication.utils.Resource
import com.example.instogramapplication.utils.ValidationUtils
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

    private fun initView(){
        // setup
        binding.apply {
            loginInlayEmail.apply {
                setTextError("Masukkan format Email yang sesuai")
                isSucces = {
                    ValidationUtils.isEmailValid(it)
                }
            }
            loginInlayPass.apply {
                setTextError("Password Kurang dari 8")
                isSucces = {
                    it?.let {
                        it.length >= 8
                    } ?: true
                }
            }
        }

    }

    private fun setupListener(){
        binding.apply {
            loginBtnLogin.setOnClickListener { handlerLogin() }
            loginTvDaftar.setOnClickListener { navigateToRegister() }
        }
    }

    private fun handlerLogin(){
        val email = binding.loginEdtEmail.text.toString().trim()
        val pass = binding.loginEdtPass.text.toString().trim()
        doLogin(email, pass)
    }

    private fun doLogin(email: String, pass: String){
        viewModel.login(email, pass)
    }

    private fun observer() {
        viewModel.loginResult.observe(this){ story ->
            when(story){
                is Resource.Loading -> {
                    // tampilkan progress bar
                }
                is Resource.Success -> {
                    // pindah ke LoginActivity atau Home
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is Resource.Error -> {
                    // tampilkan error
                    Toast.makeText(this, "kdkfkd", Toast.LENGTH_SHORT).show()
                }
                else ->{

                }
            }
        }
    }

    private fun navigateToRegister(){
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }
    companion object{
        val TAG = LoginActivity::class.java.simpleName
    }
}