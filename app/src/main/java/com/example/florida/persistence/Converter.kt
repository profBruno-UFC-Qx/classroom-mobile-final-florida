package com.example.florida.persistence

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converters {
    @TypeConverter
    fun fromBitmapToByteArray(bitmap: Bitmap?): ByteArray? {
        if (bitmap == null) return null
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun fromByteArrayToBitmap(data: ByteArray?): Bitmap? {
        if (data == null) return null
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }

    @TypeConverter
    fun fromUriToString(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun fromStringToUri(uri: String?): Uri? {
        return if (uri == null) null else Uri.parse(uri)
    }
}
