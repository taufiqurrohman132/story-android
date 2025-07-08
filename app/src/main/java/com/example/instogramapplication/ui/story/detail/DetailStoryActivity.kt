package com.example.instogramapplication.ui.story.detail

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.instogramapplication.R
import com.example.instogramapplication.data.remote.model.ListStoryItem
import com.example.instogramapplication.data.remote.model.Story
import com.example.instogramapplication.databinding.ActivityDetailStoryBinding
import com.example.instogramapplication.utils.Resource
import com.example.instogramapplication.viewmodel.UserViewModelFactory
import kotlinx.coroutines.launch

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    private val factory: UserViewModelFactory by lazy {
        UserViewModelFactory.getInstance(this)
    }
    private val viewModel: DetailStoryViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        observer()
    }

    private fun initView(){
        val detailId = intent.getStringExtra(DETAIL_ID)
        Log.d(TAG, "initView: detail id $detailId")
        if (detailId != null) {
            viewModel.loadDetailStory(detailId)
        } else {
            Toast.makeText(this, "Story ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupListener(){

    }

    private fun observer(){
        lifecycleScope.launch {
            viewModel.detailStory.collect{ result ->
                when (result) {
                    is Resource.Loading -> showLoading(true)
                    is Resource.Success -> showStories(result.data)
                    is Resource.Error -> showError(result.message)
                    else -> {}
                }
            }
        }
    }

    private fun handler(){

    }

    private fun showLoading(isLoading: Boolean){

    }

    private fun showError(message: String?){

    }

    private fun showStories(story: Story?) {
        if (story != null){
            binding.apply {
                detailUser.text = story.name
                detailDesc.text = story.description
                Glide.with(this@DetailStoryActivity)
                    .load(story.photoUrl)
                    .centerCrop()
                    .override(800)
                    .into(detailImgStory)
            }
        }
    }

    companion object{
        const val DETAIL_ID = "detail_id"
        private val TAG = DetailStoryViewModel::class.java.simpleName
    }
}