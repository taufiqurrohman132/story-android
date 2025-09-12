package com.example.instogramapplication.ui.story.detail

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.buildSpannedString
import com.example.instogramapplication.R
import com.example.instogramapplication.data.local.entity.StoryEntity
import com.example.instogramapplication.databinding.ActiviityDetailStoryBinding
import com.example.instogramapplication.ui.story.post.PostActivity
import com.example.instogramapplication.utils.ApiUtils
import com.example.instogramapplication.utils.ExtensionUtils.loadUrl

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActiviityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActiviityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setupListener()

    }


    private fun initView() {
        val story = intent.getParcelableExtra<StoryEntity>(EXTRA_DETAIL)
        showStories(story)
        binding.detailTvBerjalan.isSelected = true
    }

    private fun setupListener() {
        binding.apply {
            detailBtnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            detailImgBtnAddPost.setOnClickListener { addStory() }
        }
    }

    private fun addStory() {
        val intent = Intent(this, PostActivity::class.java)
        startActivity(intent)
    }

    private fun showStories(story: StoryEntity?) {
        if (story != null) {
            binding.apply {
                // desc n hastag
                val hashtag = this@DetailStoryActivity.getString(R.string.hastag, story.name)
                val createAt = ApiUtils.getTimeAgo(
                    this@DetailStoryActivity,
                    story.createdAt?.let {
                        story.createdAt
                    } ?: ""
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
                Log.d(TAG, "showStories: teks desc = $desc")
                Log.d(TAG, "showStories: create at = $createAt")
                detailUser.text = story.name
                detailDescExpand.apply {
                    text = desc
                    setOnClickListener {
                        toggle()
                    }
                }

                detailImgStory.loadUrl(story.photoUrl, 800)

                // profile
                story.name?.let {
                    val imgUrl = ApiUtils.avatarUrl(this@DetailStoryActivity, story.name)
                    imgProfile.loadUrl(imgUrl)
                }

            }
        }
    }

    companion object {
        const val EXTRA_DETAIL = "DETAIL"
        private val TAG = DetailStoryActivity::class.java.simpleName
    }
}