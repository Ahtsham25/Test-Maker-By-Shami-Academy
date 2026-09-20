package com.shamiacademy.papergenerator.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.shamiacademy.papergenerator.R
import com.shamiacademy.papergenerator.data.PaperCart
import com.shamiacademy.papergenerator.data.PaperMode
import com.shamiacademy.papergenerator.data.Question
import com.shamiacademy.papergenerator.network.DataRepository

/**
 * Board pattern default counts (Punjab board style). Adjust freely — this is just a starting rule.
 * mcq = how many MCQs auto-picked, short = how many short questions (pick-any-N-of-M is common in
 * real papers, but for simplicity here we auto-select this many), long = how many long questions.
 */
private val BOARD_PATTERN_COUNTS = mapOf(
    "punjab" to Triple(10, 6, 2),   // (mcq, short, long)
    "federal" to Triple(15, 8, 3)
)

class QuestionSelectionActivity : AppCompatActivity() {

    private lateinit var classId: String
    private lateinit var subjectId: String
    private lateinit var chapterId: String

    private val mcqList = mutableListOf<Question>()
    private val shortList = mutableListOf<Question>()
    private val longList = mutableListOf<Question>()

    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        classId = intent.getStringExtra("classId") ?: return
        subjectId = intent.getStringExtra("subjectId") ?: return
        chapterId = intent.getStringExtra("chapterId") ?: return
        val chapterName = intent.getStringExtra("chapterName") ?: ""
        val chapterNameUr = intent.getStringExtra("chapterNameUr") ?: ""

        PaperCart.classId = classId
        PaperCart.subjectId = subjectId
        PaperCart.subjectNameEn = intent.getStringExtra("subjectName") ?: ""
        PaperCart.subjectNameUr = intent.getStringExtra("subjectNameUr") ?: ""

        val title = findViewById<TextView>(R.id.titleText)
        title.text = "$chapterName ($chapterNameUr)"
        container = findViewById(R.id.listContainer)
        val progress = findViewById<ProgressBar>(R.id.progressBar)

        // Mode selector: Board Pattern vs Manual
        val modeRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val modeGroup = RadioGroup(this).apply { orientation = RadioGroup.HORIZONTAL }
        val boardRadio = RadioButton(this).apply { text = "بورڈ پیٹرن (خودکار)"; id = 1 }
        val manualRadio = RadioButton(this).apply { text = "مینول (خود منتخب کریں)"; id = 2; isChecked = true }
        modeGroup.addView(boardRadio)
        modeGroup.addView(manualRadio)
        modeRow.addView(modeGroup)
        container.addView(modeRow)

        val generateBtn = Button(this).apply { text = "پیپر پیش منظر / Preview Paper" }
        container.addView(generateBtn)

        val questionsContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        container.addView(questionsContainer)

        var loaded = 0
        fun checkAllLoaded() {
            loaded++
            if (loaded == 3) {
                progress.visibility = android.view.View.GONE
                renderQuestions(questionsContainer, boardRadio)
            }
        }

        DataRepository.getQuestions(classId, subjectId, chapterId, "mcq") {
            it?.questions?.let { qs -> mcqList.addAll(qs) }; checkAllLoaded()
        }
        DataRepository.getQuestions(classId, subjectId, chapterId, "short") {
            it?.questions?.let { qs -> shortList.addAll(qs) }; checkAllLoaded()
        }
        DataRepository.getQuestions(classId, subjectId, chapterId, "long") {
            it?.questions?.let { qs -> longList.addAll(qs) }; checkAllLoaded()
        }

        boardRadio.setOnCheckedChangeListener { _, checked ->
            if (checked) applyBoardPattern()
        }

        generateBtn.setOnClickListener {
            if (PaperCart.selected.isEmpty()) {
                Toast.makeText(this, "پہلے کچھ سوالات منتخب کریں۔", Toast.LENGTH_SHORT).show()
            } else {
                PaperCart.mode = if (boardRadio.isChecked) PaperMode.BOARD_PATTERN else PaperMode.MANUAL
                startActivity(Intent(this, PaperPreviewActivity::class.java))
            }
        }
    }

    private fun applyBoardPattern() {
        PaperCart.clear()
        val (m, s, l) = BOARD_PATTERN_COUNTS[PaperCart.boardPattern] ?: Triple(10, 6, 2)
        mcqList.take(m).forEach { PaperCart.selected.add(it) }
        shortList.take(s).forEach { PaperCart.selected.add(it) }
        longList.take(l).forEach { PaperCart.selected.add(it) }
        Toast.makeText(this, "بورڈ پیٹرن کے مطابق سوالات منتخب ہو گئے (${PaperCart.selected.size})", Toast.LENGTH_SHORT).show()
    }

    private fun renderQuestions(parent: LinearLayout, boardRadio: RadioButton) {
        parent.removeAllViews()

        fun addSection(titleText: String, list: List<Question>) {
            if (list.isEmpty()) return
            parent.addView(TextView(this).apply {
                text = titleText
                textSize = 16f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, 24, 0, 8)
            })
            list.forEach { q ->
                val cb = CheckBox(this).apply {
                    text = "${q.q_en}\n${q.q_ur}  (${q.marks} marks)"
                    isChecked = PaperCart.isSelected(q)
                    setOnCheckedChangeListener { _, _ -> PaperCart.toggle(q) }
                }
                parent.addView(cb)
            }
        }

        addSection("MCQs", mcqList)
        addSection("Short Questions", shortList)
        addSection("Long Questions", longList)

        if (boardRadio.isChecked) applyBoardPattern()
    }
}
