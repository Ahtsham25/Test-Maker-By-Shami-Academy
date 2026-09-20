package com.shamiacademy.papergenerator.data

/** Simple in-memory holder for the paper currently being built (app-process lifetime only). */
object PaperCart {
    val selected = mutableListOf<Question>()
    var classId: String = ""
    var subjectId: String = ""
    var subjectNameEn: String = ""
    var subjectNameUr: String = ""
    var mode: PaperMode = PaperMode.MANUAL
    var boardPattern: String = "punjab"

    fun clear() {
        selected.clear()
    }

    fun toggle(q: Question) {
        val existing = selected.find { it.id == q.id }
        if (existing != null) selected.remove(existing) else selected.add(q)
    }

    fun isSelected(q: Question) = selected.any { it.id == q.id }

    fun totalMarks() = selected.sumOf { it.marks }
}
