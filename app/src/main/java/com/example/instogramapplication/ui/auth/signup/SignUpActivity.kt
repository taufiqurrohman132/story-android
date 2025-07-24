package com.example.instogramapplication.ui.auth.signup

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.instogramapplication.R
import com.example.instogramapplication.databinding.ActivityLoginBinding
import com.example.instogramapplication.databinding.ActivitySignUpBinding
import com.example.instogramapplication.ui.auth.login.LoginActivity
import com.example.instogramapplication.utils.Resource
import com.example.instogramapplication.viewmodel.UserViewModelFactory

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private val factory: UserViewModelFactory by lazy {
        UserViewModelFactory.getInstance(this)
    }
    private val viewModel: SignUpViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setupListeners()
        observer()
    }

    // inisialize tampilan awal
    private fun initView(){
        Log.d(TAG, "initView: Starting")
        // setup
        binding.signupInlayPass.apply {
            setTextError("Password Kurang dari 8 Karakter")
            isSucces = {
                it?.let {
                    it.length >= 8
                } ?: true
            }
        }

    }

    // pasang semua click dst
    private fun setupListeners(){
        binding.apply {
            signupBtnSignUp.setOnClickListener { handleRegister() }
            signupTvDaftar.setOnClickListener { finish() }
        }
    }

    // validasi proses
    private fun handleRegister(){
        val name = binding.signupEdtName.text.toString().trim() // trim = mengahapus spasi awal n akhir
        val email = binding.signupEdtEmail.text.toString().trim()
        val password = binding.signupEdtPass.text.toString().trim()
        Log.d(TAG, "handleRegister: output $name, $email, $password")

        doRegister(name, email, password)
    }

    // proses inti
    private fun doRegister(name: String, email: String, password: String){
        viewModel.register(name, email, password)
    }

    private fun observer(){
        viewModel.apply {
            registerResult.observe(this@SignUpActivity){ result ->
                when(result){
                    is Resource.Loading -> {
                        // tampilkan progress bar
                    }
                    is Resource.Success -> {
                        // pindah ke LoginActivity atau Home
                        startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                        finish()
                    }
                    is Resource .Error -> {
                        // tampilkan error
                        Toast.makeText(this@SignUpActivity, "kdkfkd", Toast.LENGTH_SHORT).show()
                    }
                    else ->{

                    }
                }
            }
        }
    }

    companion object{
        val TAG = SignUpActivity::class.java.simpleName
    }
}