package com.example.instogramapplication.ui.story.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.instogramapplication.data.remote.model.ListStoryItem
import com.example.instogramapplication.databinding.ActiviityDetailStoryBinding
import com.example.instogramapplication.viewmodel.UserViewModelFactory

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActiviityDetailStoryBinding

    private val factory: UserViewModelFactory by lazy {
        UserViewModelFactory.getInstance(this)
    }
    private val viewModel: DetailStoryViewModel by viewModels {
        factory
    }

//    private val args: DetailStoryFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActiviityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setupListener()
        observer()

    }


    private fun initView(){
        val story = intent.getParcelableExtra<ListStoryItem>(EXTRA_DETAIL)
        Log.d(TAG, "initView: story $story")
        showStories(story)
    }

    private fun setupListener(){
        binding.apply {
            detailBtnBack.setOnClickListener { finish() }
        }
    }

    private fun observer(){
//        lifecycleScope.launch {
//            viewModel.detailStory.collect{ result ->
//                when (result) {
//                    is Resource.Loading -> showLoading(true)
//                    is Resource.Success -> showStories(result.data)
//                    is Resource.Error -> showError(result.message)
//                    else -> {}
//                }
//            }
//        }
    }

    private fun handler(){

    }

    private fun showLoading(isLoading: Boolean){

    }

    private fun showError(message: String?){

    }

    private fun showStories(story: ListStoryItem?) {
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

//    private fun detailFragment(detailId: String){
//        val args = Bundle().apply {
//            putString(DETAIL_ID, detailId)
//        }
//        findNavController().navigate(R.id.detail_story_fragment, args)
//    }

    companion object{
        const val DETAIL_ID = "detail_id"
        const val EXTRA_DETAIL = "DETAIL"
        private val TAG = DetailStoryViewModel::class.java.simpleName

////        private const val ARG_ID = "id_detail"
//
//        fun newInstance(detailId: String?): DetailStoryFragment{
//
//            return fragment
//        }

    }
}