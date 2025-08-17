package com.example.instogramapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
/*
* Kelas ini berfungsi untuk menyimpan
* informasi tentang halaman terbaru yang
*  diminta dari server. Dengan informasi ini,
*  aplikasi dapat mengidentifikasi dan meminta halaman data
*  yang tepat pada halaman selanjutnya.
* */
@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey
    val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)
