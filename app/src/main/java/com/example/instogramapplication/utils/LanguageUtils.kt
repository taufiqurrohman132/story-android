package com.example.instogramapplication.utils

import com.example.instogramapplication.model.LanguageItem

object LanguageUtils {
    private val languages = listOf(
        LanguageItem("en", "English", false),
        LanguageItem("id", "Bahasa Indonesia", false),
        LanguageItem("ar", "العربية", false),              // Arab
        LanguageItem("es", "Español", false),             // Spanyol
        LanguageItem("fr", "Français", false),            // Prancis
        LanguageItem("ja", "日本語", false),                 // Jepang
        LanguageItem("jv", "Basa Jawa", false)
    )

    fun getLanguage(selectedCode: String): List<LanguageItem> {
        return languages
            .map { it.copy(isSelected = it.code == selectedCode) }
            .sortedByDescending { it.isSelected }
    }
}