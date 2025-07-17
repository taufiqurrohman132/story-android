package com.example.instogramapplication.utils

import com.example.instogramapplication.data.local.cek.LanguageItem

object LanguageUtils {
    val languages = listOf(
        LanguageItem("en", "English", false),
        LanguageItem("id", "Bahasa Indonesia", false)
    )

    fun getLanguage(selectedCode: String): List<LanguageItem>{
        return languages
            .map { it.copy(isSelected = it.code == selectedCode)}
            .sortedByDescending { it.isSelected }
    }
}