package com.shamiacademy.papergenerator.ui

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.shamiacademy.papergenerator.R
import com.shamiacademy.papergenerator.ads.RewardedAdManager
import com.shamiacademy.papergenerator.network.DataRepository
import com.shamiacademy.papergenerator.util.UnlockManager
import com.shamiacademy.papergenerator.util.buildRow

class SubjectListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val classId = intent.getStringExtra("classId") ?: return
        val className = intent.getStringExtra("className") ?: ""
        val classNameUr = intent.getStringExtra("classNameUr") ?: ""

        val title = findViewById<TextView>(R.id.titleText)
        val container = findViewById<LinearLayout>(R.id.listContainer)
        val progress = findViewById<android.widget.ProgressBar>(R.id.progressBar)
        title.text = "$className ($classNameUr) - مضامین"

        RewardedAdManager.preload(this)

        DataRepository.getSubjects(classId) { subjects ->
            runOnUiThread {
                progress.visibility = android.view.View.GONE
                val list = subjects ?: emptyList()
                if (list.isEmpty()) {
                    container.addView(TextView(this).apply {
                        text = "ڈیٹا لوڈ نہیں ہو سکا — انٹرنیٹ چیک کریں یا بعد میں دوبارہ کوشش کریں۔"
                        setPadding(24, 24, 24, 24)
                    })
                    return@runOnUiThread
                }
                list.forEachIndexed { index, subject ->
                    val unlocked = UnlockManager.isSubjectUnlocked(this, classId, index)
                    container.addView(buildRow(this, subject.name, subject.name_ur, locked = !unlocked) {
                        if (unlocked) {
                            openChapters(classId, subject.id, subject.name, subject.name_ur)
                        } else {
                            RewardedAdManager.show(this,
                                onUnlocked = {
                                    UnlockManager.unlockSubject(this, classId, index)
                                    Toast.makeText(this, "کتاب اَن لاک ہو گئی!", Toast.LENGTH_SHORT).show()
                                    recreate()
                                },
                                onUnavailable = {
                                    Toast.makeText(this, "اشتہار ابھی دستیاب نہیں، تھوڑی دیر بعد کوشش کریں۔", Toast.LENGTH_SHORT).show()
                                })
                        }
                    })
                }
            }
        }
    }

    private fun openChapters(classId: String, subjectId: String, nameEn: String, nameUr: String) {
        val intent = Intent(this, ChapterListActivity::class.java)
        intent.putExtra("classId", classId)
        intent.putExtra("subjectId", subjectId)
        intent.putExtra("subjectName", nameEn)
        intent.putExtra("subjectNameUr", nameUr)
        startActivity(intent)
    }
}
