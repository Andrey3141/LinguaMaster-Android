/*
package com.printer.copyanki.views

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.printer.copyanki.R
import com.printer.copyanki.config.Config
import com.printer.copyanki.models.VocabularyModel
import com.printer.copyanki.utils.*
import java.util.*

class MainWindow(
    val context: Context,
    val rootView: FrameLayout
) {

    val model: VocabularyModel = VocabularyModel(context)

    var language: String
    var nativeLanguage: String
    var mode: String
    var difficulty: String = "all"
    var currentCategory: String? = null
    var learningMethod: String = "manual"
    val settings: MutableMap<String, Any> = mutableMapOf()

    private var checkInProgress = false
    private var lastCheckTime: Long = 0
    private val recentWords = mutableListOf<Map<String, Any>>()
    private val maxRecentWords = 10

    lateinit var topBar: TopBar
    lateinit var trainingPanel: TrainingPanel
    lateinit var testPanel: TestPanel
    lateinit var matchPanel: MatchPanel
    lateinit var bottomBar: BottomBar

    private lateinit var mainContainer: LinearLayout
    private lateinit var centerPanelContainer: FrameLayout
    private lateinit var categorySpinner: Spinner

    var currentPanel: Any? = null

    init {
        val savedSettings = SettingsManager.getAll()

        language = savedSettings["language"] as? String ?: Config.TRAINING_SETTINGS["default_language"] as String
        nativeLanguage = savedSettings["native_language"] as? String ?: Config.TRAINING_SETTINGS["native_language"] as String
        mode = "$language-$nativeLanguage"
        difficulty = savedSettings["difficulty"] as? String ?: "all"
        currentCategory = savedSettings["current_category"] as? String
        learningMethod = savedSettings["learning_method"] as? String ?: "manual"

        settings.putAll(Config.TRAINING_SETTINGS)
        settings.putAll(Config.SPEECH_SETTINGS)

        if (!settings.containsKey("show_hints")) {
            settings["show_hints"] = false
        }
        if (!settings.containsKey("hint_threshold")) {
            settings["hint_threshold"] = 3
        }

        val savedAppSettings = savedSettings["app_settings"] as? Map<*, *> ?: emptyMap<Any, Any>()
        for ((key, value) in savedAppSettings) {
            if (key is String && settings.containsKey(key)) {
                settings[key] = value ?: settings[key]!!
            }
        }

        if (!speechSynth.isAvailable()) {
            settings["enabled"] = false
        } else {
            speechSynth.volume = settings["volume"] as Int
            speechSynth.speed = (settings["speed"] as Double).toFloat()
            speechSynth.enabled = settings["enabled"] as Boolean
        }

        setupUI()

        if (model.getAllWords().isEmpty()) {
            val initialWords = listOf(
                Triple("hello", "привет", "en") to "ru",
                Triple("world", "мир", "en") to "ru",
                Triple("cat", "кот", "en") to "ru",
                Triple("dog", "собака", "en") to "ru",
                Triple("book", "книга", "en") to "ru"
            )

            for ((word, native) in initialWords) {
                model.addWord(word.first, word.second, word.third, native)
            }
        }
    }

    private fun setupUI() {
        val inflater = LayoutInflater.from(context)
        mainContainer = inflater.inflate(R.layout.activity_main, null) as LinearLayout
        rootView.addView(mainContainer)

        // Инициализация компонентов
        topBar = TopBar(context, this)
        val topBarContainer = mainContainer.findViewById<LinearLayout>(R.id.topBar)
        topBarContainer.removeAllViews()
        topBarContainer.addView(topBar.getView())

        bottomBar = BottomBar(context, this)
        val bottomBarContainer = mainContainer.findViewById<LinearLayout>(R.id.bottomBar)
        bottomBarContainer.removeAllViews()
        bottomBarContainer.addView(bottomBar.getView())

        centerPanelContainer = mainContainer.findViewById(R.id.centerPanelContainer)
        categorySpinner = mainContainer.findViewById(R.id.categorySpinner)

        // Настройка категорий
        val categories = mutableListOf("Все").apply { addAll(model.getAllCategories()) }
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
        categorySpinner.setSelection(if (currentCategory == null) 0 else categories.indexOf(currentCategory))

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentCategory = if (position == 0) null else categories[position]
                nextWord()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        switchTrainingPanel(learningMethod)
    }

    fun switchTrainingPanel(method: String) {
        centerPanelContainer.removeAllViews()
        currentPanel = null

        when (method) {
            "test" -> {
                testPanel = TestPanel(context, this)
                currentPanel = testPanel
                centerPanelContainer.addView(testPanel.getView())
            }
            "match" -> {
                matchPanel = MatchPanel(context, this)
                currentPanel = matchPanel
                centerPanelContainer.addView(matchPanel.getView())
            }
            else -> {
                trainingPanel = TrainingPanel(context, this)
                currentPanel = trainingPanel
                centerPanelContainer.addView(trainingPanel.getView())
            }
        }
    }

    fun changeMode(newMode: String) {
        mode = newMode
        val parts = newMode.split("-")
        if (parts.size == 2) {
            language = parts[0]
            nativeLanguage = parts[1]
        }

        when (currentPanel) {
            is TrainingPanel -> (currentPanel as TrainingPanel).updateModeIcon(language)
            is TestPanel -> (currentPanel as TestPanel).updateModeIcon(language)
            is MatchPanel -> (currentPanel as MatchPanel).updateModeIcon(language)
        }

        topBar.updateModeButtons(mode)

        model.setCurrentWord(emptyMap())
        recentWords.clear()

        checkVocabularyForCurrentLanguage()
        nextWord()
    }

    fun nextWord(): Boolean {
        if (!checkVocabularyForCurrentLanguage()) {
            return false
        }

        val studyLang = language
        val nativeLang = nativeLanguage

        if (learningMethod == "match") {
            val matchWords = generateMatchWords()
            if (matchWords != null && this::matchPanel.isInitialized) {
                matchPanel.setQuestion(matchWords)
                return true
            } else {
                Notifications.showNotification(
                    context as android.app.Activity,
                    "Мало слов",
                    "Нужно 3 слова",
                    "warning"
                )
                return false
            }
        } else {
            var allWords = model.getAllWords().filter { word ->
                (word["language"] == studyLang && word["native_language"] == nativeLang) ||
                        (word["language"] == nativeLang && word["native_language"] == studyLang)
            }

            if (currentCategory != null) {
                allWords = allWords.filter { word ->
                    (word["category"] as? String ?: "Основные") == currentCategory
                }
            }

            if (allWords.isEmpty()) {
                val message = if (currentCategory != null) {
                    "Нет в $currentCategory"
                } else {
                    "Нет слов"
                }

                when (currentPanel) {
                    is TrainingPanel -> (currentPanel as TrainingPanel).showMessage(message)
                    is TestPanel -> (currentPanel as TestPanel).showMessage(message)
                    is MatchPanel -> (currentPanel as MatchPanel).showMessage(message)
                }
                return false
            }

            var availableWords = allWords
            if (allWords.size > 1 && settings["prevent_repeats"] as Boolean) {
                val filtered = allWords.filter { it !in recentWords }
                if (filtered.isNotEmpty()) {
                    availableWords = filtered
                } else {
                    recentWords.clear()
                }
            }

            val word = availableWords.randomOrNull() ?: return false

            model.setCurrentWord(word)

            recentWords.add(word)
            if (recentWords.size > maxRecentWords) {
                recentWords.removeAt(0)
            }

            val displayWord = if (word["language"] == studyLang && word["native_language"] == nativeLang) {
                word["foreign"] as String
            } else {
                word["translation"] as String
            }

            val correctAnswer = if (word["language"] == studyLang && word["native_language"] == nativeLang) {
                word["translation"] as String
            } else {
                word["foreign"] as String
            }

            if (learningMethod == "test" && this::testPanel.isInitialized) {
                val options = generateTestOptions(word, correctAnswer, studyLang, nativeLang)
                testPanel.setQuestion(displayWord, options, correctAnswer, word)
            } else if (this::trainingPanel.isInitialized) {
                val hintText = if (settings["show_hints"] as Boolean) "" else "Перевод:"
                trainingPanel.setWord(displayWord, hintText, word)
            }

            return true
        }
    }

    fun generateTestOptions(
        correctWord: Map<String, Any>,
        correctAnswer: String,
        studyLang: String,
        nativeLang: String
    ): List<String> {
        val options = mutableListOf(correctAnswer)

        val otherWords = mutableListOf<String>()
        for (word in model.getAllWords()) {
            if (word != correctWord) {
                if (word["language"] == studyLang && word["native_language"] == nativeLang) {
                    otherWords.add(word["translation"] as String)
                } else if (word["language"] == nativeLang && word["native_language"] == studyLang) {
                    otherWords.add(word["foreign"] as String)
                }
            }
        }

        val uniqueOthers = otherWords
            .distinct()
            .filter { it.lowercase(Locale.getDefault()) != correctAnswer.lowercase(Locale.getDefault()) }

        if (uniqueOthers.isNotEmpty()) {
            val shuffled = uniqueOthers.shuffled()
            val numOptions = minOf(2, shuffled.size)
            options.addAll(shuffled.take(numOptions))
            return options.shuffled()
        }

        return listOf(correctAnswer)
    }

    fun generateMatchWords(): List<Map<String, Any>>? {
        val studyLang = language
        val nativeLang = nativeLanguage

        var allWords = model.getAllWords().filter { word ->
            (word["language"] == studyLang && word["native_language"] == nativeLang) ||
                    (word["language"] == nativeLang && word["native_language"] == studyLang)
        }

        if (currentCategory != null) {
            allWords = allWords.filter { word ->
                (word["category"] as? String ?: "Основные") == currentCategory
            }
        }

        return if (allWords.size < 3) null else allWords.shuffled().take(3)
    }

    private fun checkVocabularyForCurrentLanguage(): Boolean {
        val wordsForPair = model.getAllWords().filter { word ->
            (word["language"] == language && word["native_language"] == nativeLanguage) ||
                    (word["language"] == nativeLanguage && word["native_language"] == language)
        }

        if (wordsForPair.isEmpty()) {
            val message = "Добавьте ${Config.LANGUAGES[language]?.get("flag")}↔${Config.LANGUAGES[nativeLanguage]?.get("flag")}"

            when (currentPanel) {
                is TrainingPanel -> (currentPanel as TrainingPanel).showMessage(message)
                is TestPanel -> (currentPanel as TestPanel).showMessage(message)
                is MatchPanel -> (currentPanel as MatchPanel).showMessage(message)
            }

            Notifications.showNotification(
                context as android.app.Activity,
                "Пусто",
                message,
                "warning"
            )
            return false
        }
        return true
    }

    fun checkAnswer() {
        val currentTime = System.currentTimeMillis()
        if (checkInProgress || currentTime - lastCheckTime < 500) {
            return
        }

        if (!hasWordsForCurrentLanguage()) {
            Notifications.showNotification(
                context as android.app.Activity,
                "Нет слов",
                "Добавьте",
                "warning"
            )
            return
        }

        if (model.currentWord == null) {
            Notifications.showNotification(
                context as android.app.Activity,
                "Нет слова",
                "Выберите",
                "warning"
            )
            return
        }

        checkInProgress = true
        lastCheckTime = currentTime

        try {
            val userAnswer = trainingPanel.getUserAnswer()

            if (userAnswer.isEmpty()) {
                Notifications.showNotification(
                    context as android.app.Activity,
                    "Пусто",
                    "Введите",
                    "warning"
                )
                return
            }

            val studyLang = language
            val nativeLang = nativeLanguage
            val currentWord = model.currentWord!!

            val correctAnswer = if (currentWord["language"] == studyLang && currentWord["native_language"] == nativeLang) {
                (currentWord["translation"] as String).lowercase(Locale.getDefault())
            } else {
                (currentWord["foreign"] as String).lowercase(Locale.getDefault())
            }

            val isCorrect = userAnswer.lowercase(Locale.getDefault()) == correctAnswer

            if (isCorrect) {
                trainingPanel.showSuccessAnimation()
                Notifications.showNotification(
                    context as android.app.Activity,
                    "✓",
                    correctAnswer,
                    "success"
                )
                if (settings["auto_advance"] as Boolean) {
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        safeNextWord()
                    }, 800)
                }
            } else {
                trainingPanel.showErrorAnimation()
                Notifications.showNotification(
                    context as android.app.Activity,
                    "✗",
                    correctAnswer,
                    "error"
                )
            }

            model.checkAnswer(userAnswer, mode)
            updateStats()

        } finally {
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                checkInProgress = false
            }, 500)
        }
    }

    fun checkTestAnswer(selectedAnswer: String) {
        val currentWord = model.currentWord ?: return

        val studyLang = language
        val nativeLang = nativeLanguage

        val correctAnswer = if (currentWord["language"] == studyLang && currentWord["native_language"] == nativeLang) {
            currentWord["translation"] as String
        } else {
            currentWord["foreign"] as String
        }

        val isCorrect = selectedAnswer == correctAnswer

        if (isCorrect) {
            testPanel.showResult(true, correctAnswer)
            Notifications.showNotification(
                context as android.app.Activity,
                "✓",
                "Отлично",
                "success"
            )
            if (settings["auto_advance"] as Boolean) {
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    safeNextWord()
                }, 800)
            }
        } else {
            testPanel.showResult(false, correctAnswer)
            Notifications.showNotification(
                context as android.app.Activity,
                "✗",
                correctAnswer,
                "error"
            )
        }

        model.checkAnswer(if (isCorrect) selectedAnswer else "", mode)
        updateStats()
    }

    private fun safeNextWord() {
        try {
            val success = nextWord()
            if (!success) {
                Notifications.showNotification(
                    context as android.app.Activity,
                    "Пусто",
                    "Добавьте",
                    "info"
                )
            }
        } catch (e: Exception) {
        }
    }

    fun hasWordsForCurrentLanguage(): Boolean {
        return model.getAllWords().any { word ->
            (word["language"] == language && word["native_language"] == nativeLanguage) ||
                    (word["language"] == nativeLanguage && word["native_language"] == language)
        }
    }

    fun updateStats() {
        val stats = model.getStats()
        bottomBar.updateStats(stats)
        topBar.updateStats(stats)
    }

    fun addWord(foreign: String, translation: String, category: String = "Основные"): Boolean {
        val success = model.addWord(foreign, translation, language, nativeLanguage, category)
        if (success) {
            val categories = model.getAllCategories()
            val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item,
                mutableListOf("Все").apply { addAll(categories) })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
        }
        return success
    }

    fun showLearningMethod() {
        DialogHandlers.showLearningMethodDialog(this)
    }

    fun changeLearningMethod(method: String) {
        learningMethod = method
        switchTrainingPanel(method)
        nextWord()

        val methodNames = mapOf(
            "manual" to "ручн",
            "test" to "тест",
            "match" to "соотн"
        )

        Notifications.showNotification(
            context as android.app.Activity,
            "Метод",
            methodNames[method] ?: method,
            "success"
        )
    }

    fun applySettings() {
        if (this::trainingPanel.isInitialized) {
            trainingPanel.updateHintDisplay()
            trainingPanel.resetIncorrect()
        }
    }

    fun saveAllSettings() {
        val settingsToSave = mutableMapOf<String, Any>(
            "language" to language,
            "native_language" to nativeLanguage,
            "difficulty" to difficulty,
            "current_category" to (currentCategory ?: ""),
            "learning_method" to learningMethod,
            "app_settings" to settings.toMap()
        )
        SettingsManager.saveSettings(settingsToSave)
    }

    fun onClosing() {
        model.saveStats()
        saveAllSettings()
        speechSynth.destroy()
    }

    fun getAppContext(): Context = context

    fun addWordDialog() {
        DialogHandlers.addWordDialog(this)
    }

    fun showVocabulary() {
        VocabularyDialog.showVocabulary(this)
    }

    fun showHardWords() {
        StatsDialog.showHardWords(this)
    }

    fun showDetailedStats() {
        StatsDialog.showDetailedStats(this)
    }

    fun refreshWords() {
        nextWord()
        Notifications.showNotification(
            context as android.app.Activity,
            "Обнов",
            "Новое",
            "info"
        )
    }

    fun quickTraining() {
        Notifications.showNotification(
            context as android.app.Activity,
            "Тренир",
            "10",
            "info"
        )
        nextWord()
    }

    fun showSettingsDialog() {
        DialogHandlers.showSettingsDialog(this)
    }

    fun changeLanguageDialog() {
        DialogHandlers.changeLanguageDialog(this)
    }
}
 */