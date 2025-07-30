package com.example.instogramapplication.ui.auth.signup

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.instogramapplication.MainActivity
import com.example.instogramapplication.R
import com.example.instogramapplication.databinding.ActivityLoginBinding
import com.example.instogramapplication.databinding.ActivitySignUpBinding
import com.example.instogramapplication.ui.auth.login.LoginActivity
import com.example.instogramapplication.utils.DialogUtils
import com.example.instogramapplication.utils.DialogUtils.showToast
import com.example.instogramapplication.utils.Resource
import com.example.instogramapplication.utils.ValidationUtils
import com.example.instogramapplication.utils.constants.DialogType
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
        binding.apply {
            signupInlayEmail.apply {
                setTextError("Masukkan format Email yang sesuai")
                isSucces = {
                    ValidationUtils.isEmailValid(it)
                }
            }
            signupInlayPass.apply {
                setTextError("Password Kurang dari 8 Karakter")
                isSucces = {
                    !(!it.isNullOrBlank() && it.length < 8)
                }
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
                        result.message?.let {
                            showError(result.message)
                        }
                    }
                    is Resource.ErrorConnection -> {
                        // tampilkan error
                        showLoading(false)
                        DialogUtils.showToast(this@SignUpActivity.getString(R.string.error_koneksi), this@SignUpActivity)
                    }
                    else ->{
                        showLoading(false)
                    }
                }
            }
        }
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
        ){
            it.dismiss()
            binding.dimOverlay.visibility = View.INVISIBLE

        }
    }

    private fun showSuccess() {
        DialogUtils.stateDialog(
            this,
            DialogType.SUCCESS,
            this.getString(R.string.popup_success_title),
            this.getString(R.string.popup_error_desc),
            this.getString(R.string.popup_success_btn),
        ){
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
            finish()

            it.dismiss()
            binding.dimOverlay.visibility = View.INVISIBLE
        }
    }

    companion object{
        val TAG = SignUpActivity::class.java.simpleName
    }
}