package com.example.instogramapplication.utils

import android.content.Context
import android.util.Log
import com.example.instogramapplication.R
import com.example.instogramapplication.data.remote.model.RegisterResponse
import com.google.gson.Gson
import okhttp3.ResponseBody
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object ApiUtils {
    private val TAG = ApiUtils::class.java.simpleName

    fun parseError(errorBody: ResponseBody?): RegisterResponse? {
        return try {
            val gson = Gson()
            val jsonInString = errorBody?.charStream()
            gson.fromJson(jsonInString, RegisterResponse::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "parseError: message ", e)
            null
        }
    }

    fun getTimeAgo(context: Context, isoDate: String): String {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val dateTime = ZonedDateTime.parse(isoDate, formatter)

        // format gaya penulisan
        val now = ZonedDateTime.now(ZoneId.of("UTC"))
        val duration = Duration.between(dateTime, now)

        val seconds = duration.seconds
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val months = days / 30

        return when {
            seconds < 60 -> context.getString(R.string.time_just_now)
            minutes < 60 -> context.getString(R.string.time_minutes_ago, minutes)
            hours < 24 -> context.getString(R.string.time_hours_ago, hours)
            days < 7 -> context.getString(R.string.time_days_ago, days)
            days < 30 -> context.getString(R.string.time_weeks_ago, days / 7)
            months < 12 -> context.getString(R.string.time_months_ago, months)
            else -> {
                val formaters = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("id", "ID"))
                dateTime.format(formaters) // contoh: "Januari 2022"
            }
        }
    }

    fun avatarUrl(context: Context, name: String) =
        context.getString(R.string.avatar_base_url, name)
}