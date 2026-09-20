package com.shamiacademy.papergenerator.network

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shamiacademy.papergenerator.data.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 * Change BASE_URL to your own GitHub repo's raw content root, e.g.:
 * https://raw.githubusercontent.com/Ahtsham25/papergenerator-data/main/
 *
 * Folder layout expected on GitHub:
 *  index.json
 *  class_9/subjects.json
 *  class_9/physics/chapters.json
 *  class_9/physics/ch1/mcq.json
 *  class_9/physics/ch1/short.json
 *  class_9/physics/ch1/long.json
 *  ... same pattern for class_10
 */
object DataRepository {

    const val BASE_URL = "https://raw.githubusercontent.com/Ahtsham25/papergenerator-data/main/"

    private val client = OkHttpClient()
    private val gson = Gson()
    private val cache = mutableMapOf<String, String>()

    private fun fetchRaw(path: String, callback: (String?) -> Unit) {
        cache[path]?.let { callback(it); return }
        val request = Request.Builder().url(BASE_URL + path).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }
            override fun onResponse(call: Call, response: okhttp3.Response) {
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    cache[path] = body
                    callback(body)
                } else {
                    callback(null)
                }
            }
        })
    }

    fun getClasses(callback: (List<ClassItem>?) -> Unit) {
        fetchRaw("index.json") { json ->
            val result = json?.let {
                val type = object : TypeToken<List<ClassItem>>() {}.type
                gson.fromJson<List<ClassItem>>(it, type)
            }
            callback(result)
        }
    }

    fun getSubjects(classId: String, callback: (List<Subject>?) -> Unit) {
        fetchRaw("$classId/subjects.json") { json ->
            val result = json?.let {
                val type = object : TypeToken<List<Subject>>() {}.type
                gson.fromJson<List<Subject>>(it, type)
            }
            callback(result)
        }
    }

    fun getChapters(classId: String, subjectId: String, callback: (List<Chapter>?) -> Unit) {
        fetchRaw("$classId/$subjectId/chapters.json") { json ->
            val result = json?.let {
                val type = object : TypeToken<List<Chapter>>() {}.type
                gson.fromJson<List<Chapter>>(it, type)
            }
            callback(result)
        }
    }

    fun getQuestions(
        classId: String,
        subjectId: String,
        chapterId: String,
        type: String, // "mcq" | "short" | "long"
        callback: (QuestionFile?) -> Unit
    ) {
        fetchRaw("$classId/$subjectId/$chapterId/$type.json") { json ->
            val result = json?.let { gson.fromJson(it, QuestionFile::class.java) }
            callback(result)
        }
    }
}
