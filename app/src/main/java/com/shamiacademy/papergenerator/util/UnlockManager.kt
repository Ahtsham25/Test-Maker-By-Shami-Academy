package com.shamiacademy.papergenerator.util

import android.content.Context

/**
 * Unlock rules:
 *  - Only the FIRST subject (book) in each class is unlocked by default.
 *  - Within any unlocked subject, only the FIRST chapter is free.
 *  - Every other chapter must be unlocked individually by watching a rewarded ad.
 *  - Once unlocked (chapter or subject), it stays unlocked forever (saved in prefs).
 */
object UnlockManager {

    private const val PREFS = "paper_generator_unlocks"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun subjectKey(classId: String, subjectIndex: Int) = "subject_${classId}_$subjectIndex"
    private fun chapterKey(classId: String, subjectId: String, chapterIndex: Int) =
        "chapter_${classId}_${subjectId}_$chapterIndex"

    fun isSubjectUnlocked(context: Context, classId: String, subjectIndex: Int): Boolean {
        if (subjectIndex == 0) return true // first book always free
        return prefs(context).getBoolean(subjectKey(classId, subjectIndex), false)
    }

    fun unlockSubject(context: Context, classId: String, subjectIndex: Int) {
        prefs(context).edit().putBoolean(subjectKey(classId, subjectIndex), true).apply()
    }

    fun isChapterUnlocked(context: Context, classId: String, subjectId: String, chapterIndex: Int): Boolean {
        if (chapterIndex == 0) return true // first chapter of every subject always free
        return prefs(context).getBoolean(chapterKey(classId, subjectId, chapterIndex), false)
    }

    fun unlockChapter(context: Context, classId: String, subjectId: String, chapterIndex: Int) {
        prefs(context).edit().putBoolean(chapterKey(classId, subjectId, chapterIndex), true).apply()
    }
}
