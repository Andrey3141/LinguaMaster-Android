package com.printer.copyanki

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.printer.copyanki.config.Config
import com.printer.copyanki.models.VocabularyModel
import com.printer.copyanki.utils.SettingsManager
import com.printer.copyanki.utils.SpeechSynthesizer
import com.printer.copyanki.utils.speechSynth
import com.printer.copyanki.views.BottomBar
import com.printer.copyanki.views.ControlPanel
import com.printer.copyanki.views.ControlPanelController
import com.printer.copyanki.views.StatsPanel
import java.util.Locale

class MainActivity : AppCompatActivity(), ControlPanelController {
    private lateinit var wordLabel: TextView
    private lateinit var speakerButton: Button
    private lateinit var answerEntry: EditText
    private lateinit var checkButton: Button

    // Левая панель (статистика)
    private lateinit var statsToggleButtonContainer: LinearLayout
    private lateinit var statsToggleButtonText: TextView
    private lateinit var statsContainer: LinearLayout
    private lateinit var statsPanel: StatsPanel

    // Правая панель (управление)
    private lateinit var controlToggleButtonContainer: LinearLayout
    private lateinit var controlToggleButtonText: TextView
    private lateinit var controlContainer: LinearLayout
    private lateinit var controlPanel: ControlPanel

    // Нижняя панель (BottomBar)
    private lateinit var bottomBar: BottomBar

    private lateinit var model: VocabularyModel
    private var currentWordObj: Map<String, Any>? = null
    private var currentDisplayWord = " "

    // Состояния панелей
    private var isStatsVisible = false
    private var isControlVisible = false
    private var statsWidth = 0
    private var controlWidth = 0

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate started")

        // Инициализация
        SettingsManager.init(this)
        speechSynth = SpeechSynthesizer(this)
        model = VocabularyModel(this)

        // Находим view
        wordLabel = findViewById(R.id.wordLabel)
        speakerButton = findViewById(R.id.speakerButton)
        answerEntry = findViewById(R.id.answerEntry)
        checkButton = findViewById(R.id.checkButton)

        // Левая панель
        statsToggleButtonContainer = findViewById(R.id.statsToggleButtonContainer)
        statsToggleButtonText = findViewById(R.id.statsToggleButtonText)
        statsContainer = findViewById(R.id.statsContainer)

        // Правая панель
        controlToggleButtonContainer = findViewById(R.id.controlToggleButtonContainer)
        controlToggleButtonText = findViewById(R.id.controlToggleButtonText)
        controlContainer = findViewById(R.id.controlContainer)

        Log.d(TAG, "Views found: statsToggleButtonContainer=$statsToggleButtonContainer, controlToggleButtonContainer=$controlToggleButtonContainer")

        // Инициализируем панель статистики
        val statsPanelContainer = findViewById<LinearLayout>(R.id.statsPanel)
        statsPanel = StatsPanel(this, this)
        statsPanelContainer.removeAllViews()
        statsPanelContainer.addView(statsPanel.getView())

        // Инициализируем панель управления
        val controlPanelContainer = findViewById<LinearLayout>(R.id.controlPanel)
        controlPanel = ControlPanel(this, this)
        controlPanelContainer.removeAllViews()
        controlPanelContainer.addView(controlPanel.getView())

        // Инициализируем нижнюю панель (BottomBar)
        val bottomBarContainer = findViewById<LinearLayout>(R.id.bottomBar)
        bottomBar = BottomBar(this, this)
        bottomBarContainer.removeAllViews()
        bottomBarContainer.addView(bottomBar.getView())

        // Измеряем ширину контейнеров
        measurePanels()

        // Настройка слушателей
        setupListeners()

        // Загружаем первое слово
        loadNextWord()
    }

    private fun measurePanels() {
        statsContainer.post {
            statsContainer.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            statsWidth = statsContainer.measuredWidth
            statsContainer.translationX = -statsWidth.toFloat()
        }

        controlContainer.post {
            controlContainer.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            controlWidth = controlContainer.measuredWidth
            controlContainer.translationX = controlWidth.toFloat()
        }
    }

    private fun setupListeners() {
        speakerButton.setOnClickListener {
            speakCurrentWord()
            closeAllPanels()
        }

        answerEntry.setOnClickListener { closeAllPanels() }
        answerEntry.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) closeAllPanels()
        }

        checkButton.setOnClickListener {
            checkAnswer()
            closeAllPanels()
        }

        statsToggleButtonContainer.setOnClickListener { toggleStatsPanel() }
        controlToggleButtonContainer.setOnClickListener { toggleControlPanel() }
        findViewById<LinearLayout>(R.id.mainContent).setOnClickListener { closeAllPanels() }
    }

    private fun toggleStatsPanel() {
        if (isStatsVisible) {
            isStatsVisible = false
            ObjectAnimator.ofFloat(statsContainer, "translationX", 0f, -statsWidth.toFloat()).apply {
                duration = 300; interpolator = DecelerateInterpolator(); start()
            }.doOnEnd { statsContainer.visibility = View.GONE }
            ObjectAnimator.ofFloat(statsToggleButtonContainer, "translationX", statsWidth.toFloat(), 0f).apply {
                duration = 300; interpolator = DecelerateInterpolator(); start()
            }
            rotateText(statsToggleButtonText, 180f, 360f)
        } else {
            isStatsVisible = true
            statsContainer.visibility = View.VISIBLE
            ObjectAnimator.ofFloat(statsContainer, "translationX", -statsWidth.toFloat(), 0f).apply {
                duration = 300; interpolator = DecelerateInterpolator(); start()
            }
            ObjectAnimator.ofFloat(statsToggleButtonContainer, "translationX", 0f, statsWidth.toFloat()).apply {
                duration = 300; interpolator = DecelerateInterpolator(); start()
            }
            rotateText(statsToggleButtonText, 0f, 180f)
        }
    }

    private fun toggleControlPanel() {
        if (isControlVisible) {
            isControlVisible = false
            ObjectAnimator.ofFloat(controlContainer, "translationX", 0f, controlWidth.toFloat()).apply {
                duration = 300; interpolator = DecelerateInterpolator(); start()
            }.doOnEnd { controlContainer.visibility = View.GONE }
            ObjectAnimator.ofFloat(controlToggleButtonContainer, "translationX", -controlWidth.toFloat(), 0f).apply {
                duration = 300; interpolator = DecelerateInterpolator(); start()
            }
            rotateText(controlToggleButtonText, 180f, 360f)
        } else {
            isControlVisible = true
            controlContainer.visibility = View.VISIBLE
            ObjectAnimator.ofFloat(controlContainer, "translationX", controlWidth.toFloat(), 0f).apply {
                duration = 300; interpolator = DecelerateInterpolator(); start()
            }
            ObjectAnimator.ofFloat(controlToggleButtonContainer, "translationX", 0f, -controlWidth.toFloat()).apply {
                duration = 300; interpolator = DecelerateInterpolator(); start()
            }
            rotateText(controlToggleButtonText, 0f, 180f)
        }
    }

    private fun closeAllPanels() {
        if (isStatsVisible) {
            isStatsVisible = false
            ObjectAnimator.ofFloat(statsContainer, "translationX", 0f, -statsWidth.toFloat()).apply {
                duration = 300; interpolator = DecelerateInterpolator(); start()
            }.doOnEnd { statsContainer.visibility = View.GONE }
            ObjectAnimator.ofFloat(statsToggleButtonContainer, "translationX", statsWidth.toFloat(), 0f).apply {
                duration = 300; interpolator = DecelerateInterpolator(); start()
            }
            rotateText(statsToggleButtonText, 180f, 360f)
        }
        if (isControlVisible) {
            isControlVisible = false
            ObjectAnimator.ofFloat(controlContainer, "translationX", 0f, controlWidth.toFloat()).apply {
                duration = 300; interpolator = DecelerateInterpolator(); start()
            }.doOnEnd { controlContainer.visibility = View.GONE }
            ObjectAnimator.ofFloat(controlToggleButtonContainer, "translationX", -controlWidth.toFloat(), 0f).apply {
                duration = 300; interpolator = DecelerateInterpolator(); start()
            }
            rotateText(controlToggleButtonText, 180f, 360f)
        }
    }

    private fun rotateText(textView: TextView, from: Float, to: Float) {
        ObjectAnimator.ofFloat(textView, "rotation", from, to).apply {
            duration = 300; interpolator = DecelerateInterpolator(); start()
        }
    }

    private fun loadNextWord() {
        val word = model.getRandomWord()
        if (word != null) {
            currentWordObj = word
            currentDisplayWord = word["foreign"] as String
            wordLabel.text = currentDisplayWord
            answerEntry.text.clear()
            updateStats()
        } else {
            wordLabel.text = "Нет слов"
            currentWordObj = null
        }
    }

    private fun speakCurrentWord() {
        if (currentDisplayWord.isNotEmpty() && currentWordObj != null && speechSynth.enabled) {
            val lang = currentWordObj!!["language"] as String
            speechSynth.speakAsync(currentDisplayWord, lang)
        }
    }

    private fun checkAnswer() {
        val userAnswer = answerEntry.text.toString().trim()
        if (userAnswer.isEmpty()) {
            Toast.makeText(this, "Введите перевод", Toast.LENGTH_SHORT).show()
            return
        }
        if (currentWordObj == null) return

        val correctAnswer = currentWordObj!!["translation"] as String
        val isCorrect = userAnswer.lowercase(Locale.getDefault()) == correctAnswer.lowercase(Locale.getDefault())

        if (isCorrect) {
            Toast.makeText(this, "✅ Правильно!", Toast.LENGTH_SHORT).show()
            model.checkAnswer(userAnswer, "en-ru")
            updateStats()
            loadNextWord()
        } else {
            Toast.makeText(this, "❌ Неправильно. Правильно: $correctAnswer", Toast.LENGTH_LONG).show()
            model.checkAnswer(userAnswer, "en-ru")
            updateStats()
        }
    }

    private fun updateStats() {
        val stats = model.getStats()
        statsPanel.updateStats(stats)
        bottomBar.updateSpeechStatus()  // ← Обновляем иконку звука
    }

    // Реализация ControlPanelController
    override fun addWordDialog() = Toast.makeText(this, "Добавить слово", Toast.LENGTH_SHORT).show()
    override fun showVocabulary() = Toast.makeText(this, "Показать словарь", Toast.LENGTH_SHORT).show()
    override fun showSettingsDialog() = Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show()
    override fun refreshWords() { loadNextWord(); Toast.makeText(this, "Слова обновлены", Toast.LENGTH_SHORT).show() }
    override fun showLearningMethod() = Toast.makeText(this, "Метод обучения", Toast.LENGTH_SHORT).show()
    override fun showHardWords() = Toast.makeText(this, "Сложные слова", Toast.LENGTH_SHORT).show()
    override fun showDetailedStats() = Toast.makeText(this, "Детальная статистика", Toast.LENGTH_SHORT).show()
    override fun quickTraining() { loadNextWord(); Toast.makeText(this, "Быстрая тренировка", Toast.LENGTH_SHORT).show() }
    override fun changeLanguageDialog() = Toast.makeText(this, "Смена языка", Toast.LENGTH_SHORT).show()

    override fun onDestroy() {
        super.onDestroy()
        speechSynth.destroy()
    }

    fun isSpeechEnabled(): Boolean {
        return speechSynth.enabled
    }
}

// Extension function
fun ObjectAnimator.doOnEnd(action: () -> Unit): ObjectAnimator {
    addListener(object : android.animation.AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: android.animation.Animator) = action()
    })
    return this
}