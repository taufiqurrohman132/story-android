package com.example.instogramapplication.ui.story.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.instogramapplication.data.remote.model.ListStoryItem
import com.example.instogramapplication.databinding.ItemListStoryYBinding

class ListStoryYAdapter(
    private val context: Context,
    private val onItemClick: (ListStoryItem) -> Unit
) : ListAdapter<ListStoryItem, ListStoryYAdapter.ItemFeedViewHolder>(DIFF_CALLBACK) {

    inner class ItemFeedViewHolder(private val binding: ItemListStoryYBinding) : ViewHolder(binding.root) {
        fun bind(listStory: ListStoryItem){
            binding.apply {
                itemyTvName.text = listStory.name
                itemyTvDesk.text = buildSpannedString {
                    bold { append("${listStory.name} ") }
                    append(listStory.description)
                }
                itemyTvTime.text = listStory.createdAt

                Glide.with(context)
                    .load(listStory.photoUrl)
                    .override(400)
                    .into(itemyImgPost)

                itemyImgPost.setOnClickListener {
                    onItemClick(listStory)
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