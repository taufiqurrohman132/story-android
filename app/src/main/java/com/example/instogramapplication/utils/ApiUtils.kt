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
            minutes < 60 -> context.resources.getQuantityString(R.plurals.time_minutes_ago,
                minutes.toInt(), minutes)
            hours < 24 -> context.resources.getQuantityString(R.plurals.time_hours_ago,
                hours.toInt(), hours)
            days < 7 -> context.resources.getQuantityString(R.plurals.time_days_ago,
                days.toInt(), days)
            days < 30 -> {
                val weeks = days / 7
                context.resources.getQuantityString(R.plurals.time_weeks_ago, weeks.toInt(), weeks)
            }
            months < 12 -> context.resources.getQuantityString(R.plurals.time_months_ago,
                months.toInt(), months)
            else -> {
                val format = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("id", "ID"))
                dateTime.format(format) // contoh: "Januari 2022"
            }
        }

    }

    fun avatarUrl(context: Context, name: String) =
        context.getString(R.string.avatar_base_url, name)
}