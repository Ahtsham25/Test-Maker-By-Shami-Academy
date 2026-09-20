package com.shamiacademy.papergenerator.data

// index.json -> list of classes
data class ClassItem(
    val id: String,       // "class_9" / "class_10"
    val name: String,     // "Class 9" / "9th"
    val name_ur: String   // "نہم"
)

// class_X/subjects.json -> list of subjects for that class
data class Subject(
    val id: String,          // "physics"
    val name: String,        // "Physics"
    val name_ur: String,     // "فزکس"
    val icon: String? = null
)

// class_X/subject/chapters.json -> list of chapters
data class Chapter(
    val id: String,        // "ch1"
    val number: Int,       // 1
    val name: String,      // "Physical Quantities and Measurement"
    val name_ur: String    // "طبعی مقداریں اور پیمائش"
)

// Single question, shared shape for mcq/short/long json files
data class Question(
    val id: String,
    val type: String,          // "mcq" | "short" | "long"
    val q_en: String,
    val q_ur: String,
    val options_en: List<String>? = null,   // only for mcq
    val options_ur: List<String>? = null,
    val answer: Int? = null,                // index of correct option, mcq only
    val marks: Int,
    val board_pattern: String = "punjab"    // "punjab" | "federal"
)

// Wrapper for a question-type file, e.g. mcq.json
data class QuestionFile(
    val chapter_id: String,
    val questions: List<Question>
)

// Represents a question the user picked for the paper, keeps chosen language + marks
data class SelectedQuestion(
    val question: Question,
    val chosenMarks: Int = question.marks
)

enum class PaperMode { BOARD_PATTERN, MANUAL }
enum class Medium { URDU, ENGLISH, BILINGUAL }
