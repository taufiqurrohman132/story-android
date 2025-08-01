package com.example.instogramapplication.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instogramapplication.databinding.BottomSheetLanguageBinding
import com.example.instogramapplication.utils.LanguageUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LanguageBottomSheet(
    currentLang: String,
    private val onLangSaved: (String) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetLanguageBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterRV: LanguageAdapter
    private var selectedLang: String = currentLang

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.adapterRV = LanguageAdapter { selected ->
            selectedLang = selected.code
            updateList()
        }

        binding.rvLanguages.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@LanguageBottomSheet.adapterRV
        }

        updateList()

        binding.btnSave.setOnClickListener {
            onLangSaved(selectedLang)
            dismiss()
        }
    }

    private fun updateList() {
        val langs = LanguageUtils.getLanguage(selectedLang)
        val sortedList = langs.sortedByDescending { it.isSelected }
        adapterRV.submitList(sortedList)
    }
}