package com.example.instogramapplication.ui.story.detail

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.instogramapplication.R
import com.example.instogramapplication.data.remote.model.ListStoryItem
import com.example.instogramapplication.databinding.ActiviityDetailStoryBinding
import com.example.instogramapplication.ui.story.post.PostActivity
import com.example.instogramapplication.utils.ApiUtils
import com.example.instogramapplication.viewmodel.UserViewModelFactory
import com.google.android.material.transition.platform.MaterialContainerTransform

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActiviityDetailStoryBinding

    private val factory: UserViewModelFactory by lazy {
        UserViewModelFactory.getInstance(this)
    }
    private val viewModel: DetailStoryViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActiviityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setupListener()

    }


    private fun initView(){
        val story = intent.getParcelableExtra<ListStoryItem>(EXTRA_DETAIL)
        Log.d(TAG, "initView: story $story")
        showStories(story)
        binding.detailTvBerjalan.isSelected = true
    }

    private fun setupListener(){
        binding.apply {
            detailBtnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            detailImgBtnAddPost.setOnClickListener { addStory() }
        }
    }

    private fun observer(){
    }

    private fun handler(){

    }

    private fun showLoading(isLoading: Boolean){

    }

    private fun showError(message: String?){

    }

    private fun addStory(){
        val intent = Intent(this, PostActivity::class.java)
        startActivity(intent)
    }

    private fun showStories(story: ListStoryItem?) {
        if (story != null){
            binding.apply {
                // desc n hastag
                val hashtag = this@DetailStoryActivity.getString(R.string.hastag, story.name)
                val createAt = ApiUtils.getTimeAgo(this@DetailStoryActivity,
                    story.createdAt?.let {
                        story.createdAt
                    }?: ""
                )
                val desc = buildSpannedString {
                    append(story.description)
                    append(
                        hashtag,
                        ForegroundColorSpan(this@DetailStoryActivity.getColor(R.color.cocor_hastag)),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    append("\n $createAt")
                }
                detailUser.text = story.name
                detailDescExpand.apply {
                    text = desc
                    setOnClickListener {
                        toggle()
                        detailTvSelengkapnya.isVisible = !detailTvSelengkapnya.isVisible
                    }
                }

                Glide.with(this@DetailStoryActivity)
                    .load(story.photoUrl)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(800)
                    .into(detailImgStory)

                // click selengkapnya
                detailTvSelengkapnya.apply {
                    setOnClickListener {
                        detailDescExpand.toggle()
                        detailTvSelengkapnya.isVisible = !detailTvSelengkapnya.isVisible
                    }
                }

            }
        }
    }

    companion object{
        const val DETAIL_ID = "detail_id"
        const val EXTRA_DETAIL = "DETAIL"
        private val TAG = DetailStoryViewModel::class.java.simpleName

    }
}