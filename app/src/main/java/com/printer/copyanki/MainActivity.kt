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

    private lateinit var model: VocabularyModel
    private var currentWordObj: Map<String, Any>? = null
    private var currentDisplayWord = ""

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

        // Измеряем ширину контейнеров
        measurePanels()

        // Настройка слушателей
        setupListeners()

        // Загружаем первое слово
        loadNextWord()
    }

    private fun measurePanels() {
        statsContainer.post {
            Log.d(TAG, "Measuring stats container")
            statsContainer.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            statsWidth = statsContainer.measuredWidth
            statsContainer.translationX = -statsWidth.toFloat()
            Log.d(TAG, "Stats width = $statsWidth")
        }

        controlContainer.post {
            Log.d(TAG, "Measuring control container")
            controlContainer.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            controlWidth = controlContainer.measuredWidth
            controlContainer.translationX = controlWidth.toFloat()
            Log.d(TAG, "Control width = $controlWidth")
        }
    }

    private fun setupListeners() {
        // Кнопка озвучки
        speakerButton.setOnClickListener {
            Log.d(TAG, "Speaker button clicked")
            speakCurrentWord()
            closeAllPanels()
        }

        // Поле ввода
        answerEntry.setOnClickListener {
            Log.d(TAG, "Answer entry clicked")
            closeAllPanels()
        }

        answerEntry.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Log.d(TAG, "Answer entry focused")
                closeAllPanels()
            }
        }

        // Кнопка подтвердить
        checkButton.setOnClickListener {
            Log.d(TAG, "Check button clicked")
            checkAnswer()
            closeAllPanels()
        }

        // Кнопки открытия панелей
        statsToggleButtonContainer.setOnClickListener {
            Log.d(TAG, "Stats toggle button clicked")
            toggleStatsPanel()
        }

        controlToggleButtonContainer.setOnClickListener {
            Log.d(TAG, "Control toggle button clicked")
            toggleControlPanel()
        }

        // Основной контент
        findViewById<LinearLayout>(R.id.mainContent).setOnClickListener {
            Log.d(TAG, "Main content clicked")
            closeAllPanels()
        }
    }

    private fun toggleStatsPanel() {
        Log.d(TAG, "toggleStatsPanel called, current state: $isStatsVisible")

        if (isStatsVisible) {
            // Закрываем панель статистики
            isStatsVisible = false

            val containerAnimator = ObjectAnimator.ofFloat(statsContainer, "translationX", 0f, -statsWidth.toFloat())
            containerAnimator.duration = 300
            containerAnimator.interpolator = DecelerateInterpolator()
            containerAnimator.start()

            val buttonAnimator = ObjectAnimator.ofFloat(statsToggleButtonContainer, "translationX", statsWidth.toFloat(), 0f)
            buttonAnimator.duration = 300
            buttonAnimator.interpolator = DecelerateInterpolator()
            buttonAnimator.start()

            rotateText(statsToggleButtonText, 180f, 360f)

            containerAnimator.doOnEnd {
                statsContainer.visibility = View.GONE
                Log.d(TAG, "Stats panel hidden")
            }
        } else {
            // Открываем панель статистики
            isStatsVisible = true
            statsContainer.visibility = View.VISIBLE

            val containerAnimator = ObjectAnimator.ofFloat(statsContainer, "translationX", -statsWidth.toFloat(), 0f)
            containerAnimator.duration = 300
            containerAnimator.interpolator = DecelerateInterpolator()
            containerAnimator.start()

            val buttonAnimator = ObjectAnimator.ofFloat(statsToggleButtonContainer, "translationX", 0f, statsWidth.toFloat())
            buttonAnimator.duration = 300
            buttonAnimator.interpolator = DecelerateInterpolator()
            buttonAnimator.start()

            rotateText(statsToggleButtonText, 0f, 180f)

            Log.d(TAG, "Stats panel shown")
        }
    }

    private fun toggleControlPanel() {
        Log.d(TAG, "toggleControlPanel called, current state: $isControlVisible")

        if (isControlVisible) {
            // Закрываем панель управления
            isControlVisible = false

            val containerAnimator = ObjectAnimator.ofFloat(controlContainer, "translationX", 0f, controlWidth.toFloat())
            containerAnimator.duration = 300
            containerAnimator.interpolator = DecelerateInterpolator()
            containerAnimator.start()

            val buttonAnimator = ObjectAnimator.ofFloat(controlToggleButtonContainer, "translationX", -controlWidth.toFloat(), 0f)
            buttonAnimator.duration = 300
            buttonAnimator.interpolator = DecelerateInterpolator()
            buttonAnimator.start()

            rotateText(controlToggleButtonText, 180f, 360f)

            containerAnimator.doOnEnd {
                controlContainer.visibility = View.GONE
                Log.d(TAG, "Control panel hidden")
            }
        } else {
            // Открываем панель управления
            isControlVisible = true
            controlContainer.visibility = View.VISIBLE

            val containerAnimator = ObjectAnimator.ofFloat(controlContainer, "translationX", controlWidth.toFloat(), 0f)
            containerAnimator.duration = 300
            containerAnimator.interpolator = DecelerateInterpolator()
            containerAnimator.start()

            val buttonAnimator = ObjectAnimator.ofFloat(controlToggleButtonContainer, "translationX", 0f, -controlWidth.toFloat())
            buttonAnimator.duration = 300
            buttonAnimator.interpolator = DecelerateInterpolator()
            buttonAnimator.start()

            rotateText(controlToggleButtonText, 0f, 180f)

            Log.d(TAG, "Control panel shown")
        }
    }

    private fun closeAllPanels() {
        Log.d(TAG, "closeAllPanels called")

        if (isStatsVisible) {
            isStatsVisible = false

            val containerAnimator = ObjectAnimator.ofFloat(statsContainer, "translationX", 0f, -statsWidth.toFloat())
            containerAnimator.duration = 300
            containerAnimator.interpolator = DecelerateInterpolator()
            containerAnimator.start()

            val buttonAnimator = ObjectAnimator.ofFloat(statsToggleButtonContainer, "translationX", statsWidth.toFloat(), 0f)
            buttonAnimator.duration = 300
            buttonAnimator.interpolator = DecelerateInterpolator()
            buttonAnimator.start()

            rotateText(statsToggleButtonText, 180f, 360f)

            containerAnimator.doOnEnd {
                statsContainer.visibility = View.GONE
            }
        }

        if (isControlVisible) {
            isControlVisible = false

            val containerAnimator = ObjectAnimator.ofFloat(controlContainer, "translationX", 0f, controlWidth.toFloat())
            containerAnimator.duration = 300
            containerAnimator.interpolator = DecelerateInterpolator()
            containerAnimator.start()

            val buttonAnimator = ObjectAnimator.ofFloat(controlToggleButtonContainer, "translationX", -controlWidth.toFloat(), 0f)
            buttonAnimator.duration = 300
            buttonAnimator.interpolator = DecelerateInterpolator()
            buttonAnimator.start()

            rotateText(controlToggleButtonText, 180f, 360f)

            containerAnimator.doOnEnd {
                controlContainer.visibility = View.GONE
            }
        }
    }

    private fun rotateText(textView: TextView, from: Float, to: Float) {
        val rotationAnimator = ObjectAnimator.ofFloat(textView, "rotation", from, to)
        rotationAnimator.duration = 300
        rotationAnimator.interpolator = DecelerateInterpolator()
        rotationAnimator.start()
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
    }

    // Реализация методов интерфейса ControlPanelController
    override fun addWordDialog() {
        Toast.makeText(this, "Добавить слово", Toast.LENGTH_SHORT).show()
        // TODO: реализовать диалог добавления слова
    }

    override fun showVocabulary() {
        Toast.makeText(this, "Показать словарь", Toast.LENGTH_SHORT).show()
        // TODO: реализовать показ словаря
    }

    override fun showSettingsDialog() {
        Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show()
        // TODO: реализовать диалог настроек
    }

    override fun refreshWords() {
        loadNextWord()
        Toast.makeText(this, "Слова обновлены", Toast.LENGTH_SHORT).show()
    }

    override fun showLearningMethod() {
        Toast.makeText(this, "Метод обучения", Toast.LENGTH_SHORT).show()
        // TODO: реализовать выбор метода обучения
    }

    override fun showHardWords() {
        Toast.makeText(this, "Сложные слова", Toast.LENGTH_SHORT).show()
        // TODO: реализовать показ сложных слов
    }

    override fun showDetailedStats() {
        Toast.makeText(this, "Детальная статистика", Toast.LENGTH_SHORT).show()
        // TODO: реализовать детальную статистику
    }

    override fun quickTraining() {
        loadNextWord()
        Toast.makeText(this, "Быстрая тренировка", Toast.LENGTH_SHORT).show()
    }

    override fun changeLanguageDialog() {
        Toast.makeText(this, "Смена языка", Toast.LENGTH_SHORT).show()
        // TODO: реализовать смену языка
    }

    override fun onDestroy() {
        super.onDestroy()
        speechSynth.destroy()
    }
}

// Extension function for ObjectAnimator
fun ObjectAnimator.doOnEnd(action: () -> Unit): ObjectAnimator {
    this.addListener(object : android.animation.AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: android.animation.Animator) {
            action()
        }
    })
    return this
}