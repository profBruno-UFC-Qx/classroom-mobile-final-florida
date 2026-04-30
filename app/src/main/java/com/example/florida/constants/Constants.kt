package com.example.florida.constants

import android.graphics.Color
import android.graphics.Paint

object Constants {

}


object PaintPdf{
    val titlePaint = Paint().apply {
        textSize = 16f
        isFakeBoldText = true
    }
    val normalPaint = Paint().apply {
        textSize = 12f
        color = Color.BLACK
    }
    val bigTitlePaint = Paint().apply {
        color = Color.BLUE
        textSize = 32f
        isFakeBoldText = true
    }
    val smallLabelPaint = Paint().apply {
        textSize = 10f
        isFakeBoldText = true
        color = Color.BLACK
    }
    val tableHeaderTextPaint = Paint().apply {
        color = Color.WHITE
        textSize = 12f
        isFakeBoldText = true
    }

}