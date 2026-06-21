package com.example.florida.persistence

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class ImageStorageService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun saveImage(uri: Uri): String = withContext(Dispatchers.IO) {
        val fileName = "img_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)

        context.contentResolver.openInputStream(uri).use { input ->
            FileOutputStream(file).use { output ->
                input?.copyTo(output)
            }
        }

        file.absolutePath
    }
}
