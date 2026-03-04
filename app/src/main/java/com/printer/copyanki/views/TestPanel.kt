/*
package com.printer.copyanki.views

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import com.printer.copyanki.R
import com.printer.copyanki.config.Config
import com.printer.copyanki.utils.Animations
import com.printer.copyanki.utils.speechSynth

class TestPanel(
    private val context: Context,
    private val controller: MainWindow
) {

    private lateinit var mainFrame: LinearLayout
    private lateinit var modeLabel: TextView
    private lateinit var wordLabel: TextView
    private lateinit var speakerButton: Button
    private lateinit var hintLabel: TextView
    private lateinit var optionsFrame: LinearLayout
    private val optionButtons = mutableListOf<Button>()

    private var currentWordObj: Map<String, Any>? = null
    private var correctAnswer = ""
    private var options = listOf<String>()
    private var currentDisplayWord = ""
    private var speakerNormalBg = Color.parseColor(Config.COLORS["speaker"])

    init {
        createWidgets()
    }

    private fun createWidgets() {
        mainFrame = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(Config.COLORS["bg_card"]))
            setPadding(60, 60, 60, 60)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
        }

        val modeFrame = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Color.parseColor(Config.COLORS["bg_card"]))
        }
        mainFrame.addView(modeFrame)

        val studyFlag = Config.LANGUAGES[controller.language]?.get("flag") as String
        val nativeFlag = Config.LANGUAGES[controller.nativeLanguage]?.get("flag") as String
        modeLabel = TextView(context).apply {
            text = "$studyFlag → $nativeFlag"
            textSize = 28f
            setTextColor(Color.parseColor(Config.COLORS["text"]))
        }
        modeFrame.addView(modeLabel)

        val wordCard = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(Config.COLORS["bg_dark"]))
            setPadding(80, 80, 80, 80)
        }
        mainFrame.addView(wordCard)

        val wordFrame = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Color.parseColor(Config.COLORS["bg_dark"]))
        }
        wordCard.addView(wordFrame)

        wordLabel = TextView(context).apply {
            text = ""
            textSize = 48f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(Color.parseColor(Config.COLORS["text"]))
            setPadding(0, 0, 20, 0)
        }
        wordFrame.addView(wordLabel)

        speakerButton = Button(context).apply {
            text = "🔊"
            textSize = 24f
            setBackgroundColor(Color.parseColor(Config.COLORS["speaker"]))
            setTextColor(Color.parseColor(Config.COLORS["text"]))
            setPadding(20, 10, 20, 10)

            setOnClickListener {
                speakCurrentWord()
            }

            setOnTouchListener { v, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        if (isEnabled) {
                            setBackgroundColor(Color.parseColor(Config.COLORS["speaker_hover"]))
                        }
                    }
                    android.view.MotionEvent.ACTION_UP,
                    android.view.MotionEvent.ACTION_CANCEL -> {
                        if (isEnabled) {
                            setBackgroundColor(Color.parseColor(Config.COLORS["speaker"]))
                        } else {
                            setBackgroundColor(Color.parseColor(Config.COLORS["speaker_disabled"]))
                        }
                        performClick()
                    }
                }
                true
            }
        }
        wordFrame.addView(speakerButton)

        hintLabel = TextView(context).apply {
            text = "Выберите правильный перевод:"
            textSize = 14f
            setTextColor(Color.parseColor(Config.COLORS["text_secondary"]))
            setPadding(0, 40, 0, 20)
        }
        wordCard.addView(hintLabel)

        optionsFrame = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(Config.COLORS["bg_card"]))
        }
        mainFrame.addView(optionsFrame)

        for (i in 0..2) {
            val btn = Button(context).apply {
                text = ""
                textSize = 20f
                setBackgroundColor(Color.parseColor(Config.COLORS["bg_dark"]))
                setTextColor(Color.parseColor(Config.COLORS["text"]))
                setPadding(40, 30, 40, 30)

                setOnClickListener {
                    checkAnswer(i)
                }

                setOnTouchListener { v, event ->
                    when (event.action) {
                        android.view.MotionEvent.ACTION_DOWN -> {
                            setBackgroundColor(Color.parseColor(Config.COLORS["primary"]))
                        }
                        android.view.MotionEvent.ACTION_UP,
                        android.view.MotionEvent.ACTION_CANCEL -> {
                            if (isEnabled) {
                                setBackgroundColor(Color.parseColor(Config.COLORS["bg_dark"]))
                            }
                            performClick()
                        }
                    }
                    true
                }
            }
            optionsFrame.addView(btn)
            optionButtons.add(btn)
        }
    }

    fun setQuestion(word: String, options: List<String>, correctAnswer: String, wordObj: Map<String, Any>?) {
        currentDisplayWord = word
        wordLabel.text = word
        this.correctAnswer = correctAnswer
        this.currentWordObj = wordObj
        this.options = options

        for (btn in optionButtons) {
            btn.visibility = View.GONE
        }

        for (i in options.indices) {
            val btn = optionButtons[i]
            btn.text = options[i]
            btn.isEnabled = true
            btn.setBackgroundColor(Color.parseColor(Config.COLORS["bg_dark"]))
            btn.visibility = View.VISIBLE
        }

        if (options.size == 1) {
            hintLabel.text = "Нажмите кнопку для продолжения:"
        } else {
            hintLabel.text = "Выберите правильный перевод:"
        }

        if (word.isNotEmpty() && word != "Нет слов для изучения" && wordObj != null &&
            speechSynth.enabled && controller.settings["auto_speak"] as Boolean) {
            Handler(Looper.getMainLooper()).postDelayed({
                speakCurrentWord()
            }, 100)
        }
    }

    private fun checkAnswer(optionIndex: Int) {
        if (optionIndex >= options.size) return

        val selected = options[optionIndex]

        for (btn in optionButtons) {
            btn.isEnabled = false
        }
        speakerButton.isEnabled = false

        controller.checkTestAnswer(selected)
    }

    fun showResult(isCorrect: Boolean, correctAnswer: String) {
        if (isCorrect) {
            Animations.animateSuccess(wordLabel)
            for (btn in optionButtons) {
                if (btn.text == correctAnswer) {
                    btn.setBackgroundColor(Color.parseColor(Config.COLORS["success"]))
                }
            }
        } else {
            Animations.animateError(wordLabel)
            for (btn in optionButtons) {
                if (btn.text == correctAnswer) {
                    btn.setBackgroundColor(Color.parseColor(Config.COLORS["success"]))
                } else if (btn.text in options && btn.isEnabled) {
                    btn.setBackgroundColor(Color.parseColor(Config.COLORS["danger"]))
                }
            }
        }
    }

    fun showMessage(message: String) {
        wordLabel.text = message
        hintLabel.text = ""
        for (btn in optionButtons) {
            btn.visibility = View.GONE
        }
        speakerButton.isEnabled = false
    }

    fun showNoWordsMessage() {
        showMessage("Нет слов для изучения")
    }

    fun updateModeIcon(langCode: String) {
        val studyFlag = Config.LANGUAGES[controller.language]?.get("flag") as String
        val nativeFlag = Config.LANGUAGES[controller.nativeLanguage]?.get("flag") as String
        modeLabel.text = "$studyFlag → $nativeFlag"
    }

    fun speakCurrentWord() {
        if (!speechSynth.enabled || currentDisplayWord.isEmpty() || currentWordObj == null) return

        val lang = if (currentDisplayWord == currentWordObj!!["foreign"]) {
            currentWordObj!!["language"] as String
        } else {
            currentWordObj!!["native_language"] as String
        }

        speakerButton.isEnabled = false
        speakerButton.setBackgroundColor(Color.parseColor(Config.COLORS["speaker_disabled"]))

        speechSynth.speakAsync(currentDisplayWord, lang) { success ->
            Handler(Looper.getMainLooper()).post {
                if (currentDisplayWord.isNotEmpty() &&
                    currentDisplayWord != "Нет слов для изучения" &&
                    currentWordObj != null &&
                    speechSynth.enabled) {
                    speakerButton.isEnabled = true
                    speakerButton.setBackgroundColor(Color.parseColor(Config.COLORS["speaker"]))
                } else {
                    speakerButton.isEnabled = false
                    speakerButton.setBackgroundColor(Color.parseColor(Config.COLORS["speaker_disabled"]))
                }
            }
        }
    }

    fun getView(): LinearLayout = mainFrame
}

 */