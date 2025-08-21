package com.example.instogramapplication.ui.story.list.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.instogramapplication.R
import com.example.instogramapplication.data.local.entity.StoryEntity
import com.example.instogramapplication.databinding.ItemPostStoryBinding

class MyStoryXAdapter (
    private val context: Context,
    private val onItemClick: (ImageView, TextView, StoryEntity) -> Unit,
    private val onAddStory: () -> Unit,
) : ListAdapter<StoryEntity, MyStoryXAdapter.ItemAddViewHolder>(DIFF_CALLBACK) {
    private var currentUserName = ""

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAddViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPostStoryBinding.inflate(
            inflater,
            parent,
            false
        )
        return ItemAddViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemAddViewHolder, position: Int) {
        val story = getItem(position)
        Log.d(TAG, "onBindViewHolder: story holder = $story")
        holder.bind(story)
    }




//    fun updateUserName(userName: String?) {
//        if (!userName.isNullOrBlank()) {
//            currentUserName = userName
//            notifyItemChanged(0)
//        }
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
                return oldItem.id == newItem.id
            }
        }

        private val TAG = MyStoryXAdapter::class.java.simpleName

        private const val TYPE_STORY = 1
        private const val TYPE_ADD_STORY = 0
    }
}