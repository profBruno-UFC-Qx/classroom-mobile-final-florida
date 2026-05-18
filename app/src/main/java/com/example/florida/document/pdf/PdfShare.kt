package com.example.florida.document.pdf

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

fun sharePdf(context: Context, file: File, title: String) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, title))
}
