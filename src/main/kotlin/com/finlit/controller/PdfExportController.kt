package com.finlit.controller

import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Font
import com.lowagie.text.PageSize
import com.lowagie.text.Paragraph
import com.lowagie.text.Phrase
import com.lowagie.text.Rectangle
import com.lowagie.text.Chunk
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import com.finlit.model.entity.TransactionType
import com.finlit.repository.TransactionRepository
import com.finlit.repository.UserRepository
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.awt.Color
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Controller
@RequestMapping("/reports/export")
class PdfExportController(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) {

    private val monthNames = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )

    @GetMapping("/pdf")
    @Transactional(readOnly = true)
    fun exportPdf(
        @RequestParam(required = false) month: Int?,
        @RequestParam(required = false) year: Int?,
        @AuthenticationPrincipal userDetails: UserDetails,
        response: HttpServletResponse
    ) {
        val now           = LocalDate.now()
        val selectedMonth = month ?: now.monthValue
        val selectedYear  = year  ?: now.year
        val user          = userRepository.findByEmail(userDetails.username).orElseThrow()
        val startDate     = LocalDate.of(selectedYear, selectedMonth, 1)
        val endDate       = startDate.withDayOfMonth(startDate.lengthOfMonth())

        val transactions = transactionRepository.findByUserIdAndDateBetween(
            user.id!!, startDate, endDate)
        val totalIncome  = transactionRepository.sumByUserIdAndTypeAndDateBetween(
            user.id!!, TransactionType.INCOME, startDate, endDate)
        val totalExpense = transactionRepository.sumByUserIdAndTypeAndDateBetween(
            user.id!!, TransactionType.EXPENSE, startDate, endDate)
        val balance      = totalIncome - totalExpense
        val periodLabel  = "${monthNames[selectedMonth - 1]} $selectedYear"

        response.contentType = "application/pdf"
        response.setHeader("Content-Disposition",
            "attachment; filename=laporan-finlit-$selectedYear-${
                selectedMonth.toString().padStart(2, '0')
            }.pdf")

        val document = Document(PageSize.A4, 44f, 44f, 0f, 44f)
        PdfWriter.getInstance(document, response.outputStream)
        document.open()

        // ── Palette ──────────────────────────────
        val cDark    = Color(24,  30,  27)
        val cDark2   = Color(40,  50,  44)
        val cGold    = Color(196, 151, 58)
        val cIncome  = Color(30,  58,  42)
        val cExpense = Color(58,  28,  28)
        val cWarm    = Color(243, 241, 236)
        val cBorder  = Color(216, 211, 202)
        val cMuted   = Color(122, 138, 124)
        val cLight   = Color(154, 170, 156)
        val cWhite   = Color.WHITE
        val cRowAlt  = Color(248, 245, 242)
        val cGreenTxt= Color(45,  90,  55)
        val cRedTxt  = Color(160, 40,  40)

        // ── Fonts ─────────────────────────────────
        val fLogo   = Font(Font.HELVETICA, 22f, Font.BOLD,   cWhite)
        val fGold   = Font(Font.HELVETICA, 13f, Font.BOLD,   cGold)
        val fLight  = Font(Font.HELVETICA, 8f,  Font.NORMAL, cLight)
        val fNormal = Font(Font.HELVETICA, 8.5f,Font.NORMAL, cDark)
        val fBold   = Font(Font.HELVETICA, 8.5f,Font.BOLD,   cDark)
        val fSmall  = Font(Font.HELVETICA, 7.5f,Font.NORMAL, cMuted)
        val fTblHdr = Font(Font.HELVETICA, 6.5f,Font.BOLD,   cWhite)
        val fAmtWht = Font(Font.HELVETICA, 17f, Font.BOLD,   cWhite)
        val fAmtGld = Font(Font.HELVETICA, 17f, Font.BOLD,   cGold)
        val fLblWht = Font(Font.HELVETICA, 6f,  Font.BOLD,   cLight)
        val fIncome = Font(Font.HELVETICA, 8.5f,Font.BOLD,   cGreenTxt)
        val fExpnse = Font(Font.HELVETICA, 8.5f,Font.BOLD,   cRedTxt)
        val fFooter = Font(Font.HELVETICA, 7f,  Font.NORMAL, cMuted)
        val fSection= Font(Font.HELVETICA, 7f,  Font.BOLD,   cMuted)

        fun fmt(n: java.math.BigDecimal) = "Rp ${"%,.0f".format(n)}"

        // ── Helper: horizontal line ───────────────
        fun hLine(bg: Color, height: Float): PdfPTable {
            val t = PdfPTable(1)
            t.widthPercentage = 100f
            t.setSpacingAfter(0f)   // ← setter method
            t.setSpacingBefore(0f)  // ← setter method
            val c = PdfPCell(Phrase(" "))
            c.backgroundColor = bg
            c.fixedHeight = height
            c.border = Rectangle.NO_BORDER
            t.addCell(c)
            return t
        }

        // ════════════════════════════════════════
        // 1. HEADER — dark block
        // ════════════════════════════════════════
        val hdr = PdfPTable(2)
        hdr.widthPercentage = 100f
        hdr.setWidths(floatArrayOf(1.3f, 1f))
        hdr.setSpacingBefore(0f)   // ← setter method
        hdr.setSpacingAfter(0f)    // ← setter method

        val logoCell = PdfPCell().also { c ->
            c.backgroundColor = cDark
            c.border = Rectangle.NO_BORDER
            c.setPaddingLeft(44f); c.setPaddingRight(20f)
            c.setPaddingTop(24f);  c.setPaddingBottom(24f)
            c.addElement(Paragraph("FinLit", fLogo))
            c.addElement(Paragraph("Platform Literasi Keuangan Gen Z", fLight).also {
                it.spacingBefore = 3f
            })
        }

        val periodCell = PdfPCell().also { c ->
            c.backgroundColor = cDark
            c.border = Rectangle.NO_BORDER
            c.setPaddingLeft(20f); c.setPaddingRight(44f)
            c.setPaddingTop(24f);  c.setPaddingBottom(24f)
            c.horizontalAlignment = Element.ALIGN_RIGHT
            c.addElement(Paragraph("LAPORAN KEUANGAN",
                Font(Font.HELVETICA, 6f, Font.BOLD, cLight)).also {
                it.alignment = Element.ALIGN_RIGHT
            })
            c.addElement(Paragraph(periodLabel, fGold).also {
                it.alignment = Element.ALIGN_RIGHT
                it.spacingBefore = 4f
            })
        }

        hdr.addCell(logoCell)
        hdr.addCell(periodCell)
        document.add(hdr)
        document.add(hLine(cGold, 3f))

        // ════════════════════════════════════════
        // 2. USER INFO — warm strip
        // ════════════════════════════════════════
        val uTable = PdfPTable(1)
        uTable.widthPercentage = 100f
        uTable.setSpacingBefore(0f)   // ← setter method
        uTable.setSpacingAfter(16f)   // ← setter method

        val uCell = PdfPCell().also { c ->
            c.backgroundColor = cWarm
            c.border = Rectangle.NO_BORDER
            c.setPaddingLeft(44f); c.setPaddingRight(44f)
            c.setPaddingTop(10f);  c.setPaddingBottom(10f)
            val ph = Phrase()
            ph.add(Chunk(user.nama, fBold))
            ph.add(Chunk("     |     ", fNormal))
            ph.add(Chunk(user.email, fSmall))
            c.addElement(Paragraph(ph))
        }
        uTable.addCell(uCell)
        document.add(uTable)

        // ════════════════════════════════════════
        // 3. SUMMARY CARDS
        // ════════════════════════════════════════
        val summary = PdfPTable(3)
        summary.widthPercentage = 100f
        summary.setWidths(floatArrayOf(1f, 1f, 1f))
        summary.setSpacingAfter(20f)  // ← setter method

        fun summaryCard(
            label: String, value: String,
            bg: Color, lblFont: Font, valFont: Font
        ): PdfPCell = PdfPCell().also { c ->
            c.backgroundColor = bg
            c.border = Rectangle.NO_BORDER
            c.setPaddingLeft(44f); c.setPaddingRight(16f)
            c.setPaddingTop(18f);  c.setPaddingBottom(18f)
            c.addElement(Paragraph(label, lblFont).also { it.spacingAfter = 6f })
            c.addElement(Paragraph(value, valFont))
        }

        summary.addCell(summaryCard("TOTAL PEMASUKAN",   fmt(totalIncome),  cIncome,  fLblWht, fAmtWht))
        summary.addCell(summaryCard("TOTAL PENGELUARAN", fmt(totalExpense), cExpense, fLblWht, fAmtWht))
        summary.addCell(summaryCard("SALDO BERSIH",      fmt(balance),      cDark2,   fLblWht, fAmtGld))
        document.add(summary)

        // ════════════════════════════════════════
        // 4. TRANSACTION TABLE
        // ════════════════════════════════════════
        val secTitle = Paragraph("DETAIL TRANSAKSI", fSection)
        secTitle.spacingAfter = 6f
        document.add(secTitle)

        val txTable = PdfPTable(5)
        txTable.widthPercentage = 100f
        txTable.setWidths(floatArrayOf(1.3f, 2.0f, 1.7f, 1.5f, 2.5f))
        txTable.setSpacingAfter(20f)  // setter method

        // Header row
        listOf("TANGGAL", "DESKRIPSI", "KATEGORI", "JENIS", "JUMLAH")
            .forEachIndexed { i, hdrTxt ->
                val c = PdfPCell(Phrase(hdrTxt, fTblHdr))
                c.backgroundColor = cDark
                c.border = Rectangle.NO_BORDER
                c.setPaddingTop(9f); c.setPaddingBottom(9f)
                c.setPaddingLeft(if (i == 0) 44f else 10f)
                c.setPaddingRight(if (i == 4) 44f else 10f)
                c.horizontalAlignment =
                    if (i == 4) Element.ALIGN_RIGHT else Element.ALIGN_LEFT
                c.verticalAlignment = Element.ALIGN_MIDDLE
                txTable.addCell(c)
            }

        if (transactions.isEmpty()) {
            val ec = PdfPCell(Phrase("Tidak ada transaksi pada periode ini.", fSmall))
            ec.colspan = 5
            ec.setPadding(16f); ec.setPaddingLeft(44f)
            ec.border = Rectangle.NO_BORDER
            ec.backgroundColor = cWarm
            txTable.addCell(ec)
        } else {
            transactions.forEachIndexed { idx, t ->
                val rowBg = if (idx % 2 == 0) cWhite else cRowAlt

                fun dCell(
                    text: String,
                    align: Int = Element.ALIGN_LEFT,
                    font: Font = fNormal,
                    isFirst: Boolean = false,
                    isLast: Boolean = false
                ): PdfPCell {
                    val c = PdfPCell(Phrase(text, font))
                    c.backgroundColor = rowBg
                    c.border = Rectangle.BOTTOM
                    c.borderColor = cBorder
                    c.borderWidth = 0.5f
                    c.setPaddingTop(8f); c.setPaddingBottom(8f)
                    c.setPaddingLeft(if (isFirst) 44f else 10f)
                    c.setPaddingRight(if (isLast) 44f else 10f)
                    c.horizontalAlignment = align
                    c.verticalAlignment = Element.ALIGN_MIDDLE
                    return c
                }

                txTable.addCell(dCell(t.transactionDate.toString(), font = fSmall, isFirst = true))
                txTable.addCell(dCell(t.description, font = fBold))
                txTable.addCell(dCell(t.category.name, font = fSmall))

                if (t.type == TransactionType.INCOME) {
                    txTable.addCell(dCell("Pemasukan",       font = fIncome))
                    txTable.addCell(dCell(fmt(t.amount), Element.ALIGN_RIGHT, fIncome, isLast = true))
                } else {
                    txTable.addCell(dCell("Pengeluaran",     font = fExpnse))
                    txTable.addCell(dCell(fmt(t.amount), Element.ALIGN_RIGHT, fExpnse, isLast = true))
                }
            }
        }

        document.add(txTable)

        // ════════════════════════════════════════
        // 5. FOOTER
        // ════════════════════════════════════════
        document.add(hLine(cBorder, 0.75f))

        val footerPara = Paragraph(
            "Dicetak pada ${
                now.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id", "ID")))
            }",
            fFooter
        )
        footerPara.alignment = Element.ALIGN_CENTER
        footerPara.spacingBefore = 8f
        document.add(footerPara)

        document.close()
    }
}