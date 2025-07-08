package com.example.instogramapplication.ui.story.list

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instogramapplication.data.remote.model.ListStoryItem
import com.example.instogramapplication.databinding.ActivityListStoryBinding
import com.example.instogramapplication.ui.story.detail.DetailStoryActivity
import com.example.instogramapplication.utils.Resource
import com.example.instogramapplication.viewmodel.UserViewModelFactory
import kotlinx.coroutines.launch

class ListStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListStoryBinding
    private lateinit var adapterX: ListStoryXAdapter
    private lateinit var adapterY: ListStoryYAdapter

    private val factory: UserViewModelFactory by lazy {
        UserViewModelFactory.getInstance(this)
    }
    private val viewModel: ListStoryViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observer()
    }

    private fun initView(){

    }

    private fun setupListener(){

    }

    private fun handler(){

    }

    private fun navigateToDetail(id: String?){
        val intent = Intent(this, DetailStoryActivity::class.java)
        intent.putExtra(DetailStoryActivity.DETAIL_ID, id)
        startActivity(intent)
    }

    private fun observer(){
        lifecycleScope.launch {
            viewModel.storiesState.collect { result ->
                when (result) {
                    is Resource.Loading -> showLoading()
                    is Resource.Success -> showStories(result.data)
                    is Resource.Error -> showError(result.message)
                    else -> {}
                }
            }
        }
    }

    private fun setupRecyclerView(){
        val horiLayout = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterX = ListStoryXAdapter(this@ListStoryActivity) { story ->
            onStoryXClick()
        }
        val linearLayout = LinearLayoutManager(this)
        adapterY = ListStoryYAdapter(this){ story ->
            navigateToDetail(story.id)
        }

        binding.apply {
            rvStory.apply {
                layoutManager = horiLayout
                adapter = adapterX
            }
            rvPost.apply {
                layoutManager = linearLayout
                adapter = adapterY
            }
        }

    }

    private fun showLoading(){

    }

    private fun showError(message: String?){

    }

    private fun onStoryXClick(){

    }

    private fun showStories(data: List<ListStoryItem>?) {
        adapterX.submitList(data)
        adapterY.submitList(data)
    }
}