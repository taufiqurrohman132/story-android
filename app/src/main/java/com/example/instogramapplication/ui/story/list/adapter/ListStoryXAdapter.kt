package com.example.instogramapplication.ui.story.list.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.instogramapplication.R
import com.example.instogramapplication.data.local.entity.StoryEntity
import com.example.instogramapplication.data.remote.model.StoryItem
import com.example.instogramapplication.databinding.ItemListStoryXBinding
import com.example.instogramapplication.databinding.ItemPostStoryBinding

class ListStoryXAdapter(
    private val context: Context,
    private val onItemClick: (ImageView, TextView, StoryEntity) -> Unit,
    private val onAddStory: () -> Unit,
) : PagingDataAdapter<StoryEntity, ViewHolder>(DIFF_CALLBACK) {
//    private var myStory: StoryItem? = null
    private var currentUserName = ""

    inner class ItemStoryViewHolder(private val item: ItemListStoryXBinding) :
        ViewHolder(item.root) {
        fun bind(listStory: StoryEntity?) {
            listStory?.let {
                Glide.with(context)
                    .load(listStory.photoUrl)
                    .override(100)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .placeholder(R.drawable.profile_placeholder)
                    .circleCrop()
                    .into(item.itemStoryX)
                item.storyUsername.text = listStory.name

                item.itemStoryX.setOnClickListener {
                    onItemClick(
                        item.itemStoryX,
                        item.storyUsername,
                        listStory
                    )
                }
            }
        }
    }

    inner class ItemAddViewHolder(private val item: ItemPostStoryBinding) : ViewHolder(item.root) {
        fun bind(listStory: StoryEntity?) {
            if (listStory != null) {
                Glide.with(context)
                    .load(listStory.photoUrl)
                    .override(100)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .circleCrop()
                    .placeholder(R.drawable.profile_placeholder)
                    .into(item.itemStoryX)

                item.itemStoryX.setOnClickListener {
                    onItemClick(
                        item.itemStoryX,
                        item.storyUsername,
                        listStory
                    )
                }
            } else {
                item.itemStoryX.setOnClickListener {
                    onAddStory()
                }
            }

            item.addStory.setOnClickListener {
                onAddStory()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_ADD_STORY -> {
                val binding =
                    ItemPostStoryBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                ItemAddViewHolder(binding)
            }

            TYPE_STORY -> {
                val binding =
                    ItemListStoryXBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                ItemStoryViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_ADD_STORY else TYPE_STORY
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        val allStories = snapshot().items

        val latestMyStory = allStories
            .filter { it.name == currentUserName }
            .maxByOrNull { it.createdAt ?: "" }

        when (getItemViewType(position)) {
            TYPE_ADD_STORY -> {
                Log.d(TAG, "onBindViewHolder: my stories = $latestMyStory")
                (holder as ItemAddViewHolder).bind(latestMyStory)
            }

            TYPE_STORY -> {
                (holder as ItemStoryViewHolder).bind(story)
            }
        }
    }

    fun updateUserName(userName: String?) {
        if (!userName.isNullOrBlank()) {
            currentUserName = userName
            notifyItemChanged(0)
        }
    }

//    fun setMyStory(story: StoryItem?){
//        this.myStory = story
//        notifyItemChanged(0)
//    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: StoryEntity,
                newItem: StoryEntity
            ): Boolean {
                return oldItem == newItem
            }

        }

        private val TAG = ListStoryXAdapter::class.java.simpleName

        private const val TYPE_STORY = 1
        private const val TYPE_ADD_STORY = 0
    }
}