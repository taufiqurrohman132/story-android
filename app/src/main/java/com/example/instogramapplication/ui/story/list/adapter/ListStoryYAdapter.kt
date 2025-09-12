package com.example.instogramapplication.ui.story.list.adapter

import android.content.Context
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.instogramapplication.R
import com.example.instogramapplication.data.local.entity.StoryEntity
import com.example.instogramapplication.data.remote.model.StoryItem
import com.example.instogramapplication.databinding.ItemListStoryYBinding
import com.example.instogramapplication.utils.ApiUtils
import com.example.instogramapplication.utils.ExtensionUtils.loadUrl

class ListStoryYAdapter(
    private val context: Context,
    private val onItemClick: (ImageView, TextView, StoryEntity) -> Unit
) : PagingDataAdapter<StoryEntity, ListStoryYAdapter.ItemFeedViewHolder>(DIFF_CALLBACK) {

    inner class ItemFeedViewHolder(private val binding: ItemListStoryYBinding) :
        ViewHolder(binding.root) {
        val description = binding.itemyTvDeskExpand

        fun bind(listStory: StoryEntity  ?) {
            listStory?.let {
                binding.apply {
                    val hashtag = context.getString(R.string.hastag, listStory.name)
                    val desc = buildSpannedString {
                        bold { append("${listStory.name} ") }
                        append("${listStory.description} ")
                        append(
                            hashtag,
                            ForegroundColorSpan(context.getColor(R.color.cocor_hastag)),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    itemyTvName.text = listStory.name
                    itemyTvDeskExpand.apply {
                        text = desc
                        setOnClickListener {
                            toggle()
                        }
                    }
                    itemyTvTime.text = ApiUtils.getTimeAgo(
                        context,
                        listStory.createdAt?.let {
                            listStory.createdAt
                        } ?: ""
                    )
                    itemyTvBerjalan.isSelected = true

                    // profile
                    listStory.name?.let {
                        val imgName = ApiUtils.avatarUrl(context, listStory.name)
                        itemStoryYProfil.loadUrl(imgName)
                        imgLiked2.loadUrl(imgName)
                    }

                    itemyImgPost.loadUrl(listStory.photoUrl, 400)

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

    }
}