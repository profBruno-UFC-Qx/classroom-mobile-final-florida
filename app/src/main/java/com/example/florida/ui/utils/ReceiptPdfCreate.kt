package com.example.florida.ui.utils

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
import com.example.florida.constants.PaintPdf.bigTitlePaint
import com.example.florida.constants.PaintPdf.normalPaint
import com.example.florida.constants.PaintPdf.smallLabelPaint
import com.example.florida.constants.PaintPdf.tableHeaderTextPaint
import com.example.florida.constants.PaintPdf.titlePaint
import com.example.florida.extencions.cpfCnpjTranformer
import com.example.florida.extencions.formatForBrl
import com.example.florida.extencions.phoneTranformer
import com.example.florida.model.Client
import com.example.florida.model.Item
import com.example.florida.model.UserSetup
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun ReceiptPdfCreate(
    context: Context,
    user: UserSetup,
    cliente: Client?,
    itens: List<Item>,
    budgetNumber: Int? = null,
    dateStr: String? = null
): File {
    val pdf = PdfDocument()

    val pageWidth = 595
    val pageHeight = 842
    val marginLeft = 40
    val marginRight = 40
    val contentWidth = pageWidth - marginLeft - marginRight

    // Pre-scale images once
    val logoScaled = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(user.imagePath), 160, 80, true)

    val dateToShow = dateStr ?: LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

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

        canvas.drawText("QUANT", (marginLeft + 6).toFloat(), (startY + 20).toFloat(), tableHeaderTextPaint)
        canvas.drawText("DESCRIÇÃO DE SERVIÇO", (marginLeft + 70).toFloat(), (startY + 20).toFloat(), tableHeaderTextPaint)
        canvas.drawText("V.U", (marginLeft + 400).toFloat(), (startY + 20).toFloat(), tableHeaderTextPaint)
        canvas.drawText("TOTAL", (marginLeft + 470).toFloat(), (startY + 20).toFloat(), tableHeaderTextPaint)

        canvas.drawLine(left, bottom + 2f, right, bottom + 2f, normalPaint)

        return startY + headerHeight + 8
    }

    fun novaPagina(isFirstPage: Boolean): Pair<PdfDocument.Page, Canvas> {
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        val page = pdf.startPage(pageInfo)
        val canvas = page.canvas

        var topY = 40

        if (isFirstPage) {
            // Logo left
            canvas.drawBitmap(logoScaled, marginLeft.toFloat(), topY.toFloat(), null)

            // Big title top-right
            val titleX = marginLeft + contentWidth - 180
            canvas.drawText("ORÇAMENTO", titleX.toFloat(), (topY + 40).toFloat(), bigTitlePaint)

            // Budget number and date under title
            val labelX = titleX.toFloat()
            canvas.drawText("Nº Do Orçamento: ${budgetNumber ?: ""}", labelX, (topY + 70).toFloat(), smallLabelPaint)
            canvas.drawText("DATA:  $dateToShow", labelX, (topY + 90).toFloat(), smallLabelPaint)

            // Client info area: left and right with vertical separator
            val clientAreaTop = topY + 110
            val midX = marginLeft + contentWidth / 2

            // Left: Contratado

            canvas.drawText("CONTRATANTE: Francisco Odenio Silva Nunes", marginLeft.toFloat(), clientAreaTop.toFloat(), normalPaint)
            canvas.drawText("CPF: 938.610.953-00", marginLeft.toFloat(), (clientAreaTop + 16).toFloat(), normalPaint)
            canvas.drawText("ENDEREÇO: Matinho Dativo, n 112, Maravilha", marginLeft.toFloat(), (clientAreaTop + 32).toFloat(), normalPaint)
            canvas.drawText("CONTATO: (88) 92157-0778", marginLeft.toFloat(), (clientAreaTop + 48).toFloat(), normalPaint)


            // Vertical separator
            canvas.drawLine(midX.toFloat(), (clientAreaTop - 6).toFloat(), midX.toFloat(), (clientAreaTop + 64).toFloat(), normalPaint)

            // Right: Contratante fields with input lines
            val rightX = midX + 12
            fun drawInputLabel(label: String, yPos: Int) {
                canvas.drawText(label, rightX.toFloat(), yPos.toFloat(), smallLabelPaint)
                val lineY = yPos + 3
                canvas.drawLine(rightX.toFloat(), lineY.toFloat(), (pageWidth - marginRight).toFloat(), lineY.toFloat(), normalPaint)
            }

            drawInputLabel("CONTRATADO: ${cliente?.name ?: ""}", clientAreaTop)
            drawInputLabel("CPF/CNPJ: ${cliente?.document?.cpfCnpjTranformer() ?: ""}", clientAreaTop + 16)
            drawInputLabel("CONTATO: ${cliente?.phone?.phoneTranformer() ?: ""}", clientAreaTop + 32)
            drawInputLabel("ENDEREÇO: ${cliente?.address ?: ""}", clientAreaTop + 48)


            topY = clientAreaTop + 74
        } else {
            topY += 20
        }

        // Draw table header on every page
        y = drawTableHeader(canvas, topY)

        return page to canvas
    }

    // Start first page
    var (page, canvas) = novaPagina(isFirstPage = true)
    var totalGeral = 0.0

    itens.forEach { item ->
        val estimatedRowHeight = 40
        if (y + estimatedRowHeight > pageHeight - 140) {
            pdf.finishPage(page)
            pageNumber++
            val pair = novaPagina(isFirstPage = false)
            page = pair.first
            canvas = pair.second
        }

        val textPaint = TextPaint(normalPaint)

        val alturaDescricao = drawMultilineTex(
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
        canvas.drawText(" ${(item.price * item.qty).formatForBrl()}", 470f, centerY.toFloat(), normalPaint)

        canvas.drawLine(
            40f,
            (y + alturaDescricao + 10).toFloat(),
            (pageWidth - marginRight).toFloat(),
            (y + alturaDescricao + 10).toFloat(),
            normalPaint
        )

        totalGeral += (item.price * item.qty)
        y += alturaDescricao + 15
    }

    // Totals / observations / signature on last page
    val signatureSectionHeight = 160
    if (y + signatureSectionHeight > pageHeight - 40) {
        pdf.finishPage(page)
        pageNumber++
        val pair = novaPagina(isFirstPage = false)
        page = pair.first
        canvas = pair.second
    }

    y += 30
    canvas.drawText("VALOR TOTAL: ${totalGeral.formatForBrl()}", (pageWidth - marginRight - 200).toFloat(), y.toFloat(), titlePaint)

    // Observations box (left)
    y += 30
    val obsBoxWidth = 280

    // Signature area on right: draw a signature line and place image above it
    val signLeft = marginLeft + obsBoxWidth + 40
    val signTop = y + 10

    val sigLineY = signTop + 55f
    val sigLineLeft = signLeft.toFloat()
    val sigLineRight = (signLeft + 200).toFloat()
    canvas.drawLine(sigLineLeft, sigLineY, sigLineRight, sigLineY, normalPaint)

    // "Assinatura." label centered under the line
    val assinLabel = "Assinatura."
    val textWidth = normalPaint.measureText(assinLabel)
    val labelX = sigLineLeft + (sigLineRight - sigLineLeft - textWidth) / 2f
    canvas.drawText(assinLabel, labelX, sigLineY + 18f, normalPaint)

    // Payment note
    val paymentY = y +  13
    canvas.drawText("PAGAMENTO: A VISTA, CARTÃO, OU PIX.", marginLeft.toFloat(), paymentY.toFloat(), normalPaint)

    pdf.finishPage(page)

    val outFile = File(context.cacheDir, "orcamento.pdf")
    outFile.outputStream().use { pdf.writeTo(it) }
    pdf.close()

    return outFile
}

fun drawMultilineTex(
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
