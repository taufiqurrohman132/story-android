package com.example.instogramapplication.ui.story.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.instogramapplication.databinding.ItemLoadingStoryYBinding

class LoadingStateAdapter(private val retry: () -> Unit):
    LoadStateAdapter<LoadingStateAdapter.LoadingStateViewHolder>() {
    inner class LoadingStateViewHolder(private val binding: ItemLoadingStoryYBinding) : ViewHolder(binding.root){
        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }
        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.errorMsg.text = loadState.error.localizedMessage
            }
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.retryButton.isVisible = loadState is LoadState.Error
            binding.errorMsg.isVisible = loadState is LoadState.Error
        }
    }

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStateViewHolder {
        val binding = ItemLoadingStoryYBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingStateViewHolder(binding)
    }
}