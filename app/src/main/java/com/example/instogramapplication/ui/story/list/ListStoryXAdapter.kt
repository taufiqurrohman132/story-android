package com.example.instogramapplication.ui.story.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instogramapplication.data.remote.model.ListStoryItem
import com.example.instogramapplication.databinding.ItemListStoryXBinding

class ListStoryXAdapter(
    private val context: Context,
    private val onItemClick: (ListStoryItem) -> Unit
) : ListAdapter<ListStoryItem, ListStoryXAdapter.ItemViewHolder>(DIFF_CALLBACK)  {
    inner class ItemViewHolder(private val item: ItemListStoryXBinding) : RecyclerView.ViewHolder(item.root){
        fun bind(listStory: ListStoryItem){
            Glide.with(context)
                .load(listStory.photoUrl)
                .override(100)
                .centerCrop()
                .into(item.itemStoryX)
            item.storyUsername.text = listStory.name

            itemView.setOnClickListener {
                onItemClick(listStory)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            ItemListStoryXBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
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