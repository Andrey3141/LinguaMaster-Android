package com.printer.copyanki.models

import android.content.Context
import com.printer.copyanki.config.Config
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CategoryManager(private val context: Context) {

    private var vocabulary = mutableListOf<Map<String, Any>>()
    var currentWord: Map<String, Any>? = null
        private set

    private var dailyStats = mutableMapOf<String, Any>()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    init {
        loadVocabulary()
        loadStats()
    }

    private fun getDataPath(filename: String): String {
        val dataDir = File(context.filesDir, Config.PATHS["data"] as String)
        if (!dataDir.exists()) {
            dataDir.mkdirs()
        }
        return File(dataDir, filename).absolutePath
    }

    private fun loadVocabulary() {
        val filePath = getDataPath(Config.FILES["vocabulary"] as String)
        val file = File(filePath)

        if (file.exists()) {
            try {
                val content = file.readText()
                val jsonArray = JSONArray(content)
                vocabulary.clear()

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val wordMap = mutableMapOf<String, Any>()

                    val keys = jsonObject.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        wordMap[key] = jsonObject.get(key)
                    }

                    vocabulary.add(wordMap)
                }
            } catch (e: Exception) {
                vocabulary.clear()
            }
        } else {
            vocabulary.clear()
        }
    }

    private fun saveVocabulary() {
        val filePath = getDataPath(Config.FILES["vocabulary"] as String)
        val file = File(filePath)

        try {
            val jsonArray = JSONArray()
            for (word in vocabulary) {
                val jsonObject = JSONObject()
                for ((key, value) in word) {
                    jsonObject.put(key, value)
                }
                jsonArray.put(jsonObject)
            }

            file.parentFile?.mkdirs()
            file.writeText(jsonArray.toString(2))
        } catch (e: Exception) {
        }
    }

    private fun loadStats() {
        try {
            val filePath = getDataPath("stats.json")
            val file = File(filePath)

            if (file.exists()) {
                val content = file.readText()
                val jsonObject = JSONObject(content)
                val today = dateFormat.format(Date())

                if (jsonObject.has("date") && jsonObject.getString("date") == today) {
                    dailyStats = mutableMapOf(
                        "date" to jsonObject.getString("date"),
                        "words_today" to jsonObject.optInt("words_today", 0),
                        "correct_today" to jsonObject.optInt("correct_today", 0)
                    )
                } else {
                    dailyStats = mutableMapOf(
                        "date" to today,
                        "words_today" to 0,
                        "correct_today" to 0
                    )
                }
            } else {
                dailyStats = mutableMapOf(
                    "date" to dateFormat.format(Date()),
                    "words_today" to 0,
                    "correct_today" to 0
                )
            }
        } catch (e: Exception) {
            dailyStats = mutableMapOf(
                "date" to dateFormat.format(Date()),
                "words_today" to 0,
                "correct_today" to 0
            )
        }
    }

    private fun saveStats() {
        try {
            val filePath = getDataPath("stats.json")
            val file = File(filePath)

            val jsonObject = JSONObject()
            jsonObject.put("date", dailyStats["date"])
            jsonObject.put("words_today", dailyStats["words_today"])
            jsonObject.put("correct_today", dailyStats["correct_today"])

            file.parentFile?.mkdirs()
            file.writeText(jsonObject.toString(2))
        } catch (e: Exception) {
        }
    }

    fun setCurrentWord(word: Map<String, Any>) {
        this.currentWord = word
    }

    fun addWord(
        foreign: String,
        translation: String,
        language: String = "en",
        nativeLanguage: String = "ru"
    ): Boolean {
        val foreignTrimmed = foreign.trim()
        val translationTrimmed = translation.trim()

        for (word in vocabulary) {
            if ((word["foreign"] as String).equals(foreignTrimmed, ignoreCase = true) &&
                word["language"] == language &&
                word["native_language"] == nativeLanguage) {
                return false
            }
        }

        val newWord = mutableMapOf<String, Any>(
            "foreign" to foreignTrimmed,
            "translation" to translationTrimmed,
            "language" to language,
            "native_language" to nativeLanguage,
            "added_date" to dateFormat.format(Date()),
            "last_review" to "",
            "review_count" to 0,
            "correct_count" to 0,
            "incorrect_count" to 0,
            "difficulty" to 50
        )

        vocabulary.add(newWord)
        saveVocabulary()
        return true
    }

    fun getRandomWord(
        difficulty: String = "all",
        language: String = "en",
        nativeLanguage: String = "ru",
        preventRepeats: Boolean = true
    ): Map<String, Any>? {
        var filteredWords = vocabulary.filter { word ->
            word["language"] == language && word["native_language"] == nativeLanguage
        }.toMutableList()

        if (filteredWords.isEmpty()) {
            return null
        }

        val trainingSettings = Config.TRAINING_SETTINGS
        when (difficulty) {
            "easy" -> {
                val threshold = trainingSettings["easy_word_threshold"] as Int
                filteredWords = filteredWords.filter { w ->
                    (w["difficulty"] as Int) >= threshold
                }.toMutableList()
            }
            "hard" -> {
                val threshold = trainingSettings["hard_word_threshold"] as Int
                filteredWords = filteredWords.filter { w ->
                    (w["difficulty"] as Int) <= threshold
                }.toMutableList()
            }
            "medium" -> {
                val hardThreshold = trainingSettings["hard_word_threshold"] as Int
                val easyThreshold = trainingSettings["easy_word_threshold"] as Int
                filteredWords = filteredWords.filter { w ->
                    val diff = w["difficulty"] as Int
                    diff > hardThreshold && diff < easyThreshold
                }.toMutableList()
            }
        }

        if (filteredWords.isEmpty()) {
            filteredWords = vocabulary.filter { word ->
                word["language"] == language && word["native_language"] == nativeLanguage
            }.toMutableList()
        }

        if (filteredWords.isEmpty()) {
            return null
        }

        if (preventRepeats && currentWord != null) {
            val tempWords = filteredWords.filter { it != currentWord }.toMutableList()
            if (tempWords.isNotEmpty()) {
                filteredWords = tempWords
            }
        }

        return try {
            currentWord = filteredWords.random()
            currentWord
        } catch (e: Exception) {
            null
        }
    }

    fun checkAnswer(userAnswer: String, mode: String = "en-ru"): Pair<Boolean, String> {
        val currentWord = currentWord ?: return Pair(false, "")

        dailyStats["words_today"] = (dailyStats["words_today"] as Int) + 1

        val modeParts = mode.split("-")
        val studyLang = modeParts.getOrElse(0) { "en" }
        val nativeLang = modeParts.getOrElse(1) { "ru" }

        val correct: String = if (studyLang == currentWord["language"] && nativeLang == currentWord["native_language"]) {
            (currentWord["translation"] as String).lowercase(Locale.getDefault())
        } else {
            (currentWord["foreign"] as String).lowercase(Locale.getDefault())
        }

        val user = userAnswer.lowercase(Locale.getDefault())
        val isCorrect = user == correct

        val newWord = currentWord.toMutableMap()
        if (isCorrect) {
            newWord["correct_count"] = (newWord["correct_count"] as Int) + 1
            newWord["difficulty"] = minOf(100, (newWord["difficulty"] as Int) + 5)
            dailyStats["correct_today"] = (dailyStats["correct_today"] as Int) + 1
        } else {
            newWord["incorrect_count"] = (newWord["incorrect_count"] as Int) + 1
            newWord["difficulty"] = maxOf(0, (newWord["difficulty"] as Int) - 10)
        }

        newWord["review_count"] = (newWord["review_count"] as Int) + 1
        newWord["last_review"] = dateTimeFormat.format(Date())

        val index = vocabulary.indexOfFirst { it == currentWord }
        if (index >= 0) {
            vocabulary[index] = newWord
            this.currentWord = newWord
        }

        saveVocabulary()
        saveStats()

        return Pair(isCorrect, correct)
    }

    fun getStats(): Map<String, Any> {
        val totalWords = vocabulary.size
        val learnedWords = vocabulary.count { (it["difficulty"] as Int) >= 80 }
        val hardWords = vocabulary.count { (it["difficulty"] as Int) <= 50 }
        val progress = if (totalWords > 0) (learnedWords.toDouble() / totalWords * 100) else 0.0

        val accuracyToday = if ((dailyStats["words_today"] as Int) > 0) {
            (dailyStats["correct_today"] as Int).toDouble() / (dailyStats["words_today"] as Int) * 100
        } else 0.0

        return mapOf(
            "total_words" to totalWords,
            "learned_words" to learnedWords,
            "hard_words" to hardWords,
            "progress" to progress,
            "daily_words" to (dailyStats["words_today"] as Int),
            "correct_today" to (dailyStats["correct_today"] as Int),
            "accuracy_today" to accuracyToday
        )
    }

    fun getWordDisplay(word: Map<String, Any>, mode: String = "en-ru", displayLanguage: String? = null): String {
        val modeParts = mode.split("-")
        val studyLang = modeParts.getOrElse(0) { "en" }
        val nativeLang = modeParts.getOrElse(1) { "ru" }

        if (displayLanguage != null) {
            return if (word["language"] == displayLanguage) {
                word["foreign"] as String
            } else if (word["native_language"] == displayLanguage) {
                word["translation"] as String
            } else {
                word["foreign"] as String
            }
        }

        return if (studyLang == word["language"] && nativeLang == word["native_language"]) {
            word["foreign"] as String
        } else if (studyLang == word["native_language"] && nativeLang == word["language"]) {
            word["translation"] as String
        } else {
            word["foreign"] as String
        }
    }

    fun getDisplayWord(word: Map<String, Any>): String {
        return word["foreign"] as String
    }

    fun getDisplayTranslation(word: Map<String, Any>): String {
        return word["translation"] as String
    }

    fun getAllWords(): List<Map<String, Any>> {
        return vocabulary.toList()
    }

    fun getWordsByLanguage(language: String, nativeLanguage: String): List<Map<String, Any>> {
        return vocabulary.filter { word ->
            word["language"] == language && word["native_language"] == nativeLanguage
        }
    }

    fun getWordsForMode(mode: String): List<Map<String, Any>> {
        val modeParts = mode.split("-")
        if (modeParts.size == 2) {
            val studyLang = modeParts[0]
            val nativeLang = modeParts[1]
            return vocabulary.filter { word ->
                (word["language"] == studyLang && word["native_language"] == nativeLang) ||
                        (word["language"] == nativeLang && word["native_language"] == studyLang)
            }
        }
        return emptyList()
    }

    fun getHardWords(threshold: Int = 50): List<Map<String, Any>> {
        return vocabulary.filter { (it["difficulty"] as Int) <= threshold }
    }
}