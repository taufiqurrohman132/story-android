package com.example.instogramapplication.ui.story.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.instogramapplication.databinding.ItemLoadingStoryXBinding
import com.example.instogramapplication.databinding.ItemLoadingStoryYBinding

class LoadingStateAdapter(private val viewHolderType: Int, private val retry: () -> Unit) :
    LoadStateAdapter<ViewHolder>() {
    inner class LoadingStateViewHolderY(private val binding: ItemLoadingStoryYBinding) :
        ViewHolder(binding.root) {
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

    inner class LoadingStateViewHolderX(private val binding: ItemLoadingStoryXBinding) :
        ViewHolder(binding.root) {

        fun bind(loadState: LoadState) {
            binding.progressBar.isVisible = loadState is LoadState.Loading
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        when(holder){
            is LoadingStateViewHolderX -> holder.bind(loadState)
            is LoadingStateViewHolderY -> holder.bind(loadState)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): ViewHolder {
        return when (viewHolderType) {
            ITEM_HOLDER_X -> {
                val binding =
                    ItemLoadingStoryXBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                LoadingStateViewHolderX(binding)
            }

            else -> {
                val binding =
                    ItemLoadingStoryYBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                LoadingStateViewHolderY(binding)
            }
        }
    }

    companion object {
        const val ITEM_HOLDER_X = 1
        const val ITEM_HOLDER_Y = 2
    }
}