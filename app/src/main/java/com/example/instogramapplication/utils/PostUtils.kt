package com.example.instogramapplication.utils

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.instogramapplication.R
import com.example.instogramapplication.utils.ConvertionUtils.vectorToBitmap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PostUtils {
    private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

    fun createCustomTempFile(context: Context): File {
        val fileDir = context.externalCacheDir
        return File.createTempFile(timeStamp, ".jpg", fileDir)
    }

    fun uriToFile(imageUri: Uri, context: Context): File {
        val myFile = createCustomTempFile(context)
        val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0)
            outputStream.write(buffer, 0, length)
        outputStream.close()
        inputStream.close()
        return myFile
    }

    fun getLatestImageUri(context: Context): Uri? {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_ADDED
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val id = it.getLong(idColumn)
                return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            } else {
                Log.d("LatestImage", "Cursor kosong")
            }
        }

        return null
    }

    fun createBalloonMarker(context: Context, imageBitmap: Bitmap): BitmapDescriptor {
        // 1. Ambil background balon
        val balloonBitmap = vectorToBitmap(context.resources, R.drawable.ic_marker_balon)
        val canvas = Canvas(balloonBitmap)

        // 2. Tentukan ukuran lingkaran foto (misal 60% dari lebar balon)
        val circleSize = (balloonBitmap.width * 0.6f).toInt()

        // 3. Resize foto agar pas di lingkaran
        val scaledImage = Bitmap.createScaledBitmap(imageBitmap, circleSize, circleSize, true)

        // 4. Crop foto jadi bulat
        val output = Bitmap.createBitmap(circleSize, circleSize, Bitmap.Config.ARGB_8888)
        val photoCanvas = Canvas(output)
        val paint = Paint().apply { isAntiAlias = true }
        val rect = Rect(0, 0, circleSize, circleSize)

        photoCanvas.drawARGB(0, 0, 0, 0)
        photoCanvas.drawCircle(circleSize / 2f, circleSize / 2f, circleSize / 2f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        photoCanvas.drawBitmap(scaledImage, rect, rect, paint)

        val left = (balloonBitmap.width - circleSize) / 2f
        val top = left / 3
        canvas.drawBitmap(output, left, top, null)

        return BitmapDescriptorFactory.fromBitmap(balloonBitmap)
    }


}