package com.shamiacademy.papergenerator.ui

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds
import com.shamiacademy.papergenerator.R
import com.shamiacademy.papergenerator.ads.RewardedAdManager
import com.shamiacademy.papergenerator.data.ClassItem
import com.shamiacademy.papergenerator.network.DataRepository
import com.shamiacademy.papergenerator.util.buildRow

class ClassSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        MobileAds.initialize(this)
        RewardedAdManager.preload(this)

        val title = findViewById<TextView>(R.id.titleText)
        val container = findViewById<LinearLayout>(R.id.listContainer)
        val progress = findViewById<android.widget.ProgressBar>(R.id.progressBar)
        title.text = "Test Maker - کلاس منتخب کریں / Select Class"

        DataRepository.getClasses { classes ->
            runOnUiThread {
                progress.visibility = android.view.View.GONE
                val list = classes ?: listOf(
                    ClassItem("class_9", "Class 9", "نہم"),
                    ClassItem("class_10", "Class 10", "دہم")
                )
                list.forEach { c ->
                    container.addView(buildRow(this, c.name, c.name_ur, locked = false) {
                        val intent = Intent(this, SubjectListActivity::class.java)
                        intent.putExtra("classId", c.id)
                        intent.putExtra("className", c.name)
                        intent.putExtra("classNameUr", c.name_ur)
                        startActivity(intent)
                    })
                }
            }
        }
    }
}
