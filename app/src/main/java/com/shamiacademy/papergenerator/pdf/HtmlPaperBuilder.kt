package com.shamiacademy.papergenerator.pdf

import com.shamiacademy.papergenerator.data.Medium
import com.shamiacademy.papergenerator.data.PaperCart
import com.shamiacademy.papergenerator.data.Question

object HtmlPaperBuilder {

    fun build(
        institutionName: String,
        fontSizePx: Int,
        medium: Medium
    ): String {
        val mcqs = PaperCart.selected.filter { it.type == "mcq" }
        val shorts = PaperCart.selected.filter { it.type == "short" }
        val longs = PaperCart.selected.filter { it.type == "long" }
        val totalMarks = PaperCart.totalMarks()

        val sb = StringBuilder()
        sb.append(
            """
            <html>
            <head>
            <meta charset="utf-8">
            <style>
                @font-face {
                    font-family: 'NotoNastaliqUrdu';
                    src: url('file:///android_asset/fonts/NotoNastaliqUrdu-Regular.ttf');
                }
                body { font-family: 'NotoNastaliqUrdu', sans-serif; font-size: ${fontSizePx}px; padding: 16px; }
                .en { font-family: sans-serif; }
                .ur { font-family: 'NotoNastaliqUrdu', sans-serif; direction: rtl; text-align: right; }
                h2, h3 { text-align: center; margin: 4px 0; }
                .header-row { display: flex; justify-content: space-between; margin: 12px 0; font-size: ${fontSizePx - 2}px; }
                .question { margin: 10px 0; }
                .options { margin-inline-start: 20px; }
                hr { margin: 16px 0; }
            </style>
            </head>
            <body>
            <h2>$institutionName</h2>
            <h3>${PaperCart.subjectNameEn} / ${PaperCart.subjectNameUr}</h3>
            <div class="header-row">
                <span>Name: ___________________</span>
                <span>Roll No: _______</span>
            </div>
            <div class="header-row">
                <span>Time: _______</span>
                <span>Total Marks: $totalMarks</span>
            </div>
            <hr>
            """.trimIndent()
        )

        if (mcqs.isNotEmpty()) {
            sb.append("<h3>Section A - MCQs / معروضی سوالات</h3>")
            mcqs.forEachIndexed { i, q -> sb.append(renderQuestion(i + 1, q, medium, showOptions = true)) }
        }
        if (shorts.isNotEmpty()) {
            sb.append("<h3>Section B - Short Questions / مختصر سوالات</h3>")
            shorts.forEachIndexed { i, q -> sb.append(renderQuestion(i + 1, q, medium, showOptions = false)) }
        }
        if (longs.isNotEmpty()) {
            sb.append("<h3>Section C - Long Questions / تفصیلی سوالات</h3>")
            longs.forEachIndexed { i, q -> sb.append(renderQuestion(i + 1, q, medium, showOptions = false)) }
        }

        sb.append("</body></html>")
        return sb.toString()
    }

    private fun renderQuestion(number: Int, q: Question, medium: Medium, showOptions: Boolean): String {
        val sb = StringBuilder()
        sb.append("<div class='question'>")
        when (medium) {
            Medium.ENGLISH -> sb.append("<div class='en'>$number. ${q.q_en} <i>(${q.marks} marks)</i></div>")
            Medium.URDU -> sb.append("<div class='ur'>$number. ${q.q_ur} <i>(${q.marks} نمبر)</i></div>")
            Medium.BILINGUAL -> {
                sb.append("<div class='en'>$number. ${q.q_en} <i>(${q.marks} marks)</i></div>")
                sb.append("<div class='ur'>${q.q_ur}</div>")
            }
        }
        if (showOptions && q.options_en != null) {
            sb.append("<div class='options en'>")
            q.options_en.forEachIndexed { idx, opt ->
                sb.append("${('A' + idx)}. $opt&nbsp;&nbsp;&nbsp;")
            }
            sb.append("</div>")
            if (medium != Medium.ENGLISH && q.options_ur != null) {
                sb.append("<div class='options ur'>")
                q.options_ur.forEachIndexed { idx, opt ->
                    sb.append("${('A' + idx)}. $opt&nbsp;&nbsp;&nbsp;")
                }
                sb.append("</div>")
            }
        }
        sb.append("</div>")
        return sb.toString()
    }
}
