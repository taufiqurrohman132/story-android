package com.example.instogramapplication.data.remote.model

import android.os.Parcelable
import com.example.instogramapplication.data.local.entity.StoryEntity
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryResponse(

    @field:SerializedName("listStory")
    val listStory: List<StoryItem> = emptyList(),

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
) : Parcelable

@Parcelize
data class StoryItem(

    @field:SerializedName("photoUrl")
    val photoUrl: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("lon")
    val lon: Double? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("lat")
    val lat: Double? = null
) : Parcelable {
    @IgnoredOnParcel
    var isExpaned: Boolean = false

    fun toEntity() = StoryEntity(id ?: "0", photoUrl, createdAt, name, description, lon, lat)
}
