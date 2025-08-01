package com.example.instogramapplication.ui.story.list

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.instogramapplication.R
import com.example.instogramapplication.data.remote.model.ListStoryItem
import com.example.instogramapplication.databinding.ItemListStoryYBinding
import com.example.instogramapplication.ui.story.detail.DetailStoryActivity
import com.example.instogramapplication.utils.ApiUtils
import com.google.android.material.imageview.ShapeableImageView

class ListStoryYAdapter(
    private val context: Context,
    private val onItemClick: (ImageView, TextView, ListStoryItem) -> Unit
) : ListAdapter<ListStoryItem, ListStoryYAdapter.ItemFeedViewHolder>(DIFF_CALLBACK) {

    private val TAG = ListStoryYAdapter::class.java.simpleName

    inner class ItemFeedViewHolder(private val binding: ItemListStoryYBinding) : ViewHolder(binding.root) {
        val description = binding.itemyTvDeskExpand

        fun bind(listStory: ListStoryItem){
            binding.apply {
                val hashtag = context.getString(R.string.hastag, listStory.name)
                val desc = buildSpannedString {
                    bold { append("${listStory.name} ") }
                    append(listStory.description)
                    append(hashtag, ForegroundColorSpan(context.getColor(R.color.cocor_hastag)), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                itemyTvName.text = listStory.name
                itemyTvDeskExpand.apply {
                    text = desc
                    setOnClickListener {
                        toggle()
                    }
                }
                itemyTvTime.text = ApiUtils.getTimeAgo(context,
                    listStory.createdAt?.let {
                        listStory.createdAt
                    }?: ""
                )
                itemyTvBerjalan.isSelected = true

                // profile
                listStory.name?.let {
                    val imgName = ApiUtils.avatarUrl(context, listStory.name)
                    Glide.with(context)
                        .load(imgName)
                        .into(itemStoryYProfil)

                    // liked
                    Glide.with(context)
                        .load(imgName)
                        .into(imgLiked2)

                    Log.d(TAG, "bind: avatar url $imgName")
                }

                Glide.with(context)
                    .load(listStory.photoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(400)
                    .into(itemyImgPost)

                itemyImgPost.setOnClickListener {
                    binding.apply {
                        onItemClick(
                            itemyImgPost,
                            itemyTvDeskExpand,
                            listStory
                        )
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemFeedViewHolder {
        val view = ItemListStoryYBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ItemFeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemFeedViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
    }

    override fun onViewRecycled(holder: ItemFeedViewHolder) {
        super.onViewRecycled(holder)

        // reset to false is expaned
        val position = holder.adapterPosition
        if (position != RecyclerView.NO_POSITION)
            holder.apply {
                description.collapse()
            }
    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>(){
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }

        }

    }
}