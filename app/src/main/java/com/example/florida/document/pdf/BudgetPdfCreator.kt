package com.example.florida.document.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.example.florida.R
import com.example.florida.constants.PaintPdf.bigTitlePaint
import com.example.florida.constants.PaintPdf.normalPaint
import com.example.florida.constants.PaintPdf.smallLabelPaint
import com.example.florida.constants.PaintPdf.tableHeaderTextPaint
import com.example.florida.constants.PaintPdf.titlePaint
import com.example.florida.extensions.cpfCnpjTranformer
import com.example.florida.extensions.formatForBrl
import com.example.florida.extensions.phoneTranformer
import com.example.florida.domain.model.Client
import com.example.florida.domain.model.UserSetup
import com.example.florida.domain.model.Item
import java.io.File
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

fun BudgetPdfCreator(
    user: UserSetup,
    client: Client?,
    itens: List<Item>,
    observasion: String,
    date: OffsetDateTime?,
    budgetNumber: String?,
    prazo: String?,
    validade: String?,
    context: Context
): File{
    val pdf = PdfDocument()
    val pageWidth = 595
    val pageHeight = 842
    val marginLeft = 40
    val marginRight = 40
    val contentWidth = pageWidth - marginLeft - marginRight

    val logoScaled = user.imagePath
        ?.let { BitmapFactory.decodeFile(it) }
        ?.let { Bitmap.createScaledBitmap(it, 160, 80, true) }

    val dateToShow = date?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    var pageNumber = 1
    var y = 0


    fun drawTableHeader(canvas: Canvas, startY: Int): Int {
        val headerHeight = 28
        val left = marginLeft.toFloat()
        val right = (pageWidth - marginRight).toFloat()
        val top = startY.toFloat()
        val bottom = (startY + headerHeight).toFloat()

        val rectPaint = Paint().apply { color = Color.BLACK }
        canvas.drawRect(left, top, right, bottom, rectPaint)

        canvas.drawText(context.getString(R.string.pdf_qty), (marginLeft + 6).toFloat(), (startY + 20).toFloat(), tableHeaderTextPaint)
        canvas.drawText(context.getString(R.string.pdf_service_description), (marginLeft + 70).toFloat(), (startY + 20).toFloat(), tableHeaderTextPaint)
        canvas.drawText(context.getString(R.string.pdf_unit_value), (marginLeft + 400).toFloat(), (startY + 20).toFloat(), tableHeaderTextPaint)
        canvas.drawText(context.getString(R.string.pdf_total), (marginLeft + 470).toFloat(), (startY + 20).toFloat(), tableHeaderTextPaint)

        canvas.drawLine(left, bottom + 2f, right, bottom + 2f, normalPaint)

        return startY + headerHeight + 8
    }

    fun newPage(
        isFirstPage: Boolean
    ): Pair<PdfDocument.Page, Canvas>{
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        val page = pdf.startPage(pageInfo)
        val canvas = page.canvas

        var topY = 40

        if (isFirstPage) {
            logoScaled?.let { canvas.drawBitmap(it, marginLeft.toFloat(), topY.toFloat(), null) }

            val titleX = marginLeft + contentWidth - 180
            canvas.drawText(context.getString(R.string.pdf_budget), titleX.toFloat(), (topY + 40).toFloat(), bigTitlePaint)

            val labelX = titleX.toFloat()
            canvas.drawText(context.getString(R.string.pdf_budget_number, budgetNumber ?: ""), labelX, (topY + 70).toFloat(), smallLabelPaint)
            canvas.drawText(context.getString(R.string.pdf_date, dateToShow), labelX, (topY + 90).toFloat(), smallLabelPaint)

            val clientAreaTop = topY + 110
            val midX = marginLeft + contentWidth / 2


            canvas.drawText(context.getString(R.string.pdf_contractor, user.name), marginLeft.toFloat(), clientAreaTop.toFloat(), normalPaint)
            canvas.drawText(context.getString(R.string.pdf_cpf, user.document.cpfCnpjTranformer()), marginLeft.toFloat(), (clientAreaTop + 16).toFloat(), normalPaint)
            canvas.drawText(context.getString(R.string.pdf_address, "${user.street}, ${user.number}, ${user.city}"), marginLeft.toFloat(), (clientAreaTop + 32).toFloat(), normalPaint)
            canvas.drawText(context.getString(R.string.pdf_contact, user.phone.phoneTranformer()), marginLeft.toFloat(), (clientAreaTop + 48).toFloat(), normalPaint)

            canvas.drawLine(midX.toFloat(), (clientAreaTop - 6).toFloat(), midX.toFloat(), (clientAreaTop + 64).toFloat(), normalPaint)

            val rightX = midX + 12
            fun drawInputLabel(label: String, yPos: Int) {
                canvas.drawText(label, rightX.toFloat(), yPos.toFloat(), smallLabelPaint)
                val lineY = yPos + 3
                canvas.drawLine(rightX.toFloat(), lineY.toFloat(), (pageWidth - marginRight).toFloat(), lineY.toFloat(), normalPaint)
            }

            drawInputLabel(context.getString(R.string.pdf_hired, client?.name ?: ""), clientAreaTop)
            drawInputLabel(context.getString(R.string.pdf_document, client?.document?.cpfCnpjTranformer() ?: ""), clientAreaTop + 16)
            drawInputLabel(context.getString(R.string.pdf_contact, client?.phone?.phoneTranformer() ?: ""), clientAreaTop + 32)
            drawInputLabel(context.getString(R.string.pdf_address, client?.address ?: ""), clientAreaTop + 48)


            topY = clientAreaTop + 74
        } else {
            topY += 20
        }

        y = drawTableHeader(canvas, topY)

        return page to canvas
    }

    var (page, canvas) = newPage(isFirstPage = true)

    var totalMoney = 0L

    itens.forEach { item ->
        val estimatedRowHeight = 40
        if (y + estimatedRowHeight > pageHeight - 140) {
            pdf.finishPage(page)
            pageNumber++
            val pair = newPage(isFirstPage = false)
            page = pair.first
            canvas = pair.second
        }

        val textPaint = TextPaint(normalPaint)

        val alturaDescricao = drawMultilineText(
            canvas = canvas,
            text = item.description,
            x = 100f,
            y = y.toFloat(),
            maxWidth = 220,
            paint = textPaint
        )

        val centerY = y + alturaDescricao / 2 + 5

        canvas.drawText(item.qty.toString(), 40f, centerY.toFloat(), normalPaint)
        canvas.drawText(" ${item.price.formatForBrl()}", 350f, centerY.toFloat(), normalPaint)
        canvas.drawText(" ${item.total.formatForBrl()}", 470f, centerY.toFloat(), normalPaint)

        canvas.drawLine(
            40f,
            (y + alturaDescricao + 10).toFloat(),
            (pageWidth - marginRight).toFloat(),
            (y + alturaDescricao + 10).toFloat(),
            normalPaint
        )

        totalMoney += item.total
        y += alturaDescricao + 15
    }

    // Totals / observations / signature on last page
    val signatureSectionHeight = 160
    if (y + signatureSectionHeight > pageHeight - 40) {
        pdf.finishPage(page)
        pageNumber++
        val pair = newPage(isFirstPage = false)
        page = pair.first
        canvas = pair.second
    }

    y += 30
    canvas.drawText(context.getString(R.string.pdf_total_value, totalMoney.formatForBrl()), (pageWidth - marginRight - 200).toFloat(), y.toFloat(), titlePaint)

    // Observations box (left)
    y += 30
    val obsBoxTop = y
    val obsBoxLeft = marginLeft
    val obsBoxWidth = 280
    val obsBoxHeight = 80
    val obsRectPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f
        color = Color.BLACK
    }
    canvas.drawRect(
        obsBoxLeft.toFloat(),
        obsBoxTop.toFloat(),
        (obsBoxLeft + obsBoxWidth).toFloat(),
        (obsBoxTop + obsBoxHeight).toFloat(),
        obsRectPaint
    )
    canvas.drawText(context.getString(R.string.pdf_observations), (obsBoxLeft + 8).toFloat(), (obsBoxTop + 16).toFloat(), normalPaint)

    // printar as observações dentro da caixa
    observasion?.let {
        drawMultilineText(
            canvas = canvas,
            text = it,
            x = (obsBoxLeft + 8).toFloat(),
            y = (obsBoxTop + 32).toFloat(),
            maxWidth = obsBoxWidth - 16,
            paint = TextPaint(normalPaint)
        )
    }


    // Signature area on right: draw a signature line and place image above it
    val signLeft = marginLeft + obsBoxWidth + 40
    val signTop = obsBoxTop + 10
    // Draw signature bitmap (if available) slightly above the signature line
    //canvas.drawBitmap(assinaturaScaled, signLeft.toFloat(), signTop.toFloat(), null)

    val sigLineY = signTop + 55f
    val sigLineLeft = signLeft.toFloat()
    val sigLineRight = (signLeft + 200).toFloat()
    canvas.drawLine(sigLineLeft, sigLineY, sigLineRight, sigLineY, normalPaint)

    // "Assinatura." label centered under the line
    val assinLabel = context.getString(R.string.pdf_signature)
    val textWidth = normalPaint.measureText(assinLabel)
    val labelX = sigLineLeft + (sigLineRight - sigLineLeft - textWidth) / 2f
    canvas.drawText(assinLabel, labelX, sigLineY + 18f, normalPaint)

    // Payment note
    val paymentY = obsBoxTop + obsBoxHeight + 13
    canvas.drawText(context.getString(R.string.pdf_payment), marginLeft.toFloat(), paymentY.toFloat(), normalPaint)

    canvas.drawText(context.getString(R.string.pdf_delivery_time, prazo ?: ""), marginLeft.toFloat(), paymentY.toFloat() + 13, normalPaint)
    canvas.drawText(context.getString(R.string.pdf_budget_validity, validade ?: ""), marginLeft.toFloat(), paymentY.toFloat() + 26, normalPaint)

    pdf.finishPage(page)

    val outFile = File(context.cacheDir, "orcamento.pdf")
    outFile.outputStream().use { pdf.writeTo(it) }
    pdf.close()

    return outFile
}

fun drawMultilineText(
    canvas: Canvas,
    text: String,
    x: Float,
    y: Float,
    maxWidth: Int,
    paint: TextPaint
): Int {
    val staticLayout = StaticLayout.Builder
        .obtain(text, 0, text.length, paint, maxWidth)
        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
        .setLineSpacing(0f, 1f)
        .setIncludePad(false)
        .build()

    canvas.save()
    canvas.translate(x, y)
    staticLayout.draw(canvas)
    canvas.restore()

    return staticLayout.height
}
