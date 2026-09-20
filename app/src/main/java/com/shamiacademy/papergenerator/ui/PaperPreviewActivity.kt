package com.shamiacademy.papergenerator.ui

import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.shamiacademy.papergenerator.R
import com.shamiacademy.papergenerator.data.Medium
import com.shamiacademy.papergenerator.pdf.HtmlPaperBuilder

class PaperPreviewActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paper_preview)

        webView = findViewById(R.id.paperWebView)
        val institutionInput = findViewById<EditText>(R.id.institutionNameInput)
        val fontSeekBar = findViewById<SeekBar>(R.id.fontSizeSeekBar)
        val mediumSpinner = findViewById<Spinner>(R.id.mediumSpinner)
        val refreshBtn = findViewById<Button>(R.id.refreshPreviewBtn)
        val printBtn = findViewById<Button>(R.id.printBtn)

        institutionInput.setText("Shami Academy")

        val mediumOptions = listOf("Urdu + English (Bilingual)", "Urdu Only", "English Only")
        mediumSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mediumOptions)

        fun currentMedium(): Medium = when (mediumSpinner.selectedItemPosition) {
            1 -> Medium.URDU
            2 -> Medium.ENGLISH
            else -> Medium.BILINGUAL
        }

        fun renderPaper() {
            val fontSizePx = 12 + fontSeekBar.progress // base 12px .. up to 30px
            val html = HtmlPaperBuilder.build(
                institutionName = institutionInput.text.toString().ifBlank { "Shami Academy" },
                fontSizePx = fontSizePx,
                medium = currentMedium()
            )
            webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null)
        }

        refreshBtn.setOnClickListener { renderPaper() }
        renderPaper()

        printBtn.setOnClickListener {
            val printManager = getSystemService(PRINT_SERVICE) as PrintManager
            val jobName = "${institutionInput.text} Paper"
            val printAdapter = webView.createPrintDocumentAdapter(jobName)
            printManager.print(
                jobName,
                printAdapter,
                PrintAttributes.Builder().build()
            )
            // In the system print dialog the user picks "Save as PDF" as the printer,
            // which saves the finished PDF straight to their device.
        }
    }
}
