package com.shamiacademy.papergenerator.util

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

/** Builds a simple Material-ish card row for lists (classes/subjects/chapters). */
fun buildRow(
    context: Context,
    titleEn: String,
    titleUr: String,
    locked: Boolean,
    onClick: () -> Unit
): LinearLayout {
    val row = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(28, 28, 28, 28)
        setBackgroundColor(Color.WHITE)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 12)
        layoutParams = params
        isClickable = true
        isFocusable = true
    }

    val textCol = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
    }
    textCol.addView(TextView(context).apply {
        text = titleEn
        textSize = 16f
        setTextColor(Color.parseColor("#212121"))
    })
    textCol.addView(TextView(context).apply {
        text = titleUr
        textSize = 14f
        setTextColor(Color.parseColor("#616161"))
    })
    row.addView(textCol)

    row.addView(TextView(context).apply {
        text = if (locked) "🔒" else "›"
        textSize = 18f
        setTextColor(if (locked) Color.parseColor("#E53935") else Color.parseColor("#1565C0"))
    })

    row.setOnClickListener { onClick() }
    return row
}
