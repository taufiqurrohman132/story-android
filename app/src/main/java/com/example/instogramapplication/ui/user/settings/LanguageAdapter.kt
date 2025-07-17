package com.example.instogramapplication.ui.user.settings

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.instogramapplication.R
import com.example.instogramapplication.data.local.cek.LanguageItem
import com.example.instogramapplication.databinding.ItemLanguageBinding

class LanguageAdapter(
    private val onSelect: (LanguageItem) -> Unit
) : ListAdapter<LanguageItem, LanguageAdapter.ItemViewHolder>(DiffCallback) {
    inner class ItemViewHolder(private val binding: ItemLanguageBinding): ViewHolder(binding.root) {
        fun bind(language: LanguageItem){
            binding.radioButtonLanguage.apply { 
                text = language.name
                isChecked = language.isSelected
            }
            Log.d("TAG", "bind: langunge ${binding.radioButtonLanguage.isSelected}")
            itemView.setOnClickListener { onSelect(language) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemLanguageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        Log.d("TAG", "onBindViewHolder: bottom sheet ")
        holder.bind(
            getItem(position)
        )
    }
  
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<LanguageItem>() {
            override fun areItemsTheSame(old: LanguageItem, new: LanguageItem) =
                old.code == new.code
            override fun areContentsTheSame(old: LanguageItem, new: LanguageItem) =
                old == new
        }
    }
}