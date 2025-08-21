package com.example.instogramapplication.ui.story.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instogramapplication.databinding.ItemHeaderListStoryYBinding

class HeaderListStoryYAdapter(private val storyXAdapter: ConcatAdapter) :
    RecyclerView.Adapter<HeaderListStoryYAdapter.HeaderViewHolder>(){
    inner class HeaderViewHolder(val binding: ItemHeaderListStoryYBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.rvHeaderStoryY.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = storyXAdapter
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val binding = ItemHeaderListStoryYBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeaderViewHolder(binding)
    }
    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount() = 1 // selalu ada 1 header
}