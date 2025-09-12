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
import com.example.instogramapplication.databinding.ItemListStoryXBinding

class ListStoryXAdapter(
    private val context: Context,
    private val onItemClick: (ImageView, TextView, StoryEntity) -> Unit,
) : PagingDataAdapter<StoryEntity, ListStoryXAdapter.ItemStoryViewHolder>(DIFF_CALLBACK) {

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

    override fun onBindViewHolder(holder: ItemStoryViewHolder, position: Int) {
        val story = getItem(position)
        Log.d(TAG, "onBindViewHolder: story holder = $story")
        holder.bind(story)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemStoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =
            ItemListStoryXBinding.inflate(
                inflater,
                parent,
                false
            )
        return ItemStoryViewHolder(binding)
    }

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

        private val TAG = ListStoryXAdapter::class.java.simpleName

    }
}