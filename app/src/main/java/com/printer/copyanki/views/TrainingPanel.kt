/*
package com.printer.copyanki.views

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.printer.copyanki.R
import com.printer.copyanki.config.Config
import com.printer.copyanki.utils.Animations
import com.printer.copyanki.utils.speechSynth

class TrainingPanel(
    private val context: Context,
    private val controller: MainWindow
) {

    private lateinit var view: LinearLayout
    private lateinit var modeLabel: TextView
    private lateinit var wordLabel: TextView
    private lateinit var speakerButton: Button
    private lateinit var hintLabel: TextView
    private lateinit var answerEntry: EditText
    private lateinit var checkButton: Button

    private var currentDisplayWord = ""
    private var currentWordObj: Map<String, Any>? = null
    private val speakerNormalBg = Color.parseColor(Config.COLORS["speaker"])
    private var incorrectAttempts = 0
    private var hintActive = false
    private var originalEntryBg = Color.parseColor(Config.COLORS["bg_card"])
    private var originalEntryFg = Color.parseColor(Config.COLORS["text"])
    private var hintText = ""
    private var userTyping = false

    init {
        setupView()
    }

    private fun setupView() {
        view = LayoutInflater.from(context).inflate(R.layout.training_panel, null) as LinearLayout

        modeLabel = view.findViewById(R.id.modeLabel)
        wordLabel = view.findViewById(R.id.wordLabel)
        speakerButton = view.findViewById(R.id.speakerButton)
        hintLabel = view.findViewById(R.id.hintLabel)
        answerEntry = view.findViewById(R.id.answerEntry)
        checkButton = view.findViewById(R.id.checkButton)

        updateModeIcon(controller.language)

        speakerButton.setOnClickListener {
            speakCurrentWord()
        }

        checkButton.setOnClickListener {
            controller.checkAnswer()
        }

        answerEntry.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                controller.checkAnswer()
                true
            } else {
                false
            }
        }

        answerEntry.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (hintActive) {
                    userTyping = true
                    clearHint()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty() && !userTyping && hintText.isNotEmpty() &&
                    incorrectAttempts >= (controller.settings["hint_threshold"] as Int)) {
                    restoreHint()
                }
            }
        })

        updateSpeakerButtonState()
    }

    fun setWord(word: String, hint: String, wordObj: Map<String, Any>?) {
        currentDisplayWord = word
        currentWordObj = wordObj
        wordLabel.text = word
        incorrectAttempts = 0
        hintActive = false
        hintText = ""
        userTyping = false

        answerEntry.setText("")
        answerEntry.setBackgroundColor(originalEntryBg)
        answerEntry.setTextColor(originalEntryFg)

        updateHintDisplay()
        updateSpeakerButtonState()

        if (word.isNotEmpty() && word != "Нет слов" && wordObj != null &&
            speechSynth.enabled && controller.settings["auto_speak"] as Boolean) {
            Handler(Looper.getMainLooper()).postDelayed({
                speakCurrentWord()
            }, 100)
        }
    }

    fun showMessage(message: String) {
        wordLabel.text = message
        wordLabel.textSize = 18f
        hintLabel.text = ""
        answerEntry.isEnabled = false
        checkButton.isEnabled = false
        speakerButton.isEnabled = false
    }

    fun updateHintDisplay() {
        if (controller.settings["show_hints"] as Boolean) {
            val threshold = controller.settings["hint_threshold"] as Int
            val remaining = maxOf(0, threshold - incorrectAttempts)

            if (remaining > 0) {
                hintLabel.text = "Подсказка через $remaining"
                hintLabel.setTextColor(Color.parseColor(Config.COLORS["text_secondary"]))
            } else {
                if (hintActive) {
                    hintLabel.text = "Подсказка активна"
                    hintLabel.setTextColor(Color.parseColor(Config.COLORS["warning"]))
                } else {
                    hintLabel.text = "Подсказка готова"
                    hintLabel.setTextColor(Color.parseColor(Config.COLORS["warning"]))
                }
            }
        } else {
            hintLabel.text = "Введите перевод:"
            hintLabel.setTextColor(Color.parseColor(Config.COLORS["text_secondary"]))
        }
    }

    fun showHint() {
        if (currentWordObj == null || hintActive) return

        val studyLang = controller.language
        val nativeLang = controller.nativeLanguage

        val correctAnswer = if (currentWordObj!!["language"] == studyLang &&
            currentWordObj!!["native_language"] == nativeLang) {
            currentWordObj!!["translation"] as String
        } else {
            currentWordObj!!["foreign"] as String
        }

        hintText = correctAnswer

        answerEntry.setText(correctAnswer)
        answerEntry.setBackgroundColor(originalEntryBg)
        answerEntry.setTextColor(Color.parseColor(Config.COLORS["text_secondary"]))
        hintActive = true

        updateHintDisplay()
    }

    fun clearHint() {
        if (hintActive) {
            answerEntry.setText("")
            answerEntry.setBackgroundColor(originalEntryBg)
            answerEntry.setTextColor(originalEntryFg)
            hintActive = false
            updateHintDisplay()
        }
    }

    fun restoreHint() {
        if (hintText.isNotEmpty() && !userTyping && !hintActive) {
            answerEntry.setText(hintText)
            answerEntry.setBackgroundColor(originalEntryBg)
            answerEntry.setTextColor(Color.parseColor(Config.COLORS["text_secondary"]))
            hintActive = true
            updateHintDisplay()
        }
    }

    fun incrementIncorrect() {
        if (controller.settings["show_hints"] as Boolean) {
            incorrectAttempts++
            val threshold = controller.settings["hint_threshold"] as Int

            if (incorrectAttempts >= threshold && !hintActive && !userTyping) {
                showHint()
            } else {
                updateHintDisplay()
            }
        }
    }

    fun resetIncorrect() {
        incorrectAttempts = 0
        if (hintActive) {
            clearHint()
        } else {
            updateHintDisplay()
        }
    }

    fun updateSpeakerButtonState() {
        if (currentDisplayWord.isNotEmpty() &&
            currentDisplayWord != "Нет слов" &&
            currentWordObj != null &&
            speechSynth.enabled &&
            speechSynth.isAvailable()) {
            speakerButton.isEnabled = true
            speakerButton.setBackgroundColor(Color.parseColor(Config.COLORS["speaker"]))
        } else {
            speakerButton.isEnabled = false
            speakerButton.setBackgroundColor(Color.parseColor(Config.COLORS["speaker_disabled"]))
        }
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
                    currentDisplayWord != "Нет слов" &&
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

    fun getUserAnswer(): String {
        return answerEntry.text.toString().trim()
    }

    fun focusEntry() {
        answerEntry.requestFocus()
    }

    fun showSuccessAnimation() {
        Animations.animateSuccess(wordLabel)
        resetIncorrect()
    }

    fun showErrorAnimation() {
        Animations.animateError(wordLabel)
        incrementIncorrect()
    }

    fun updateModeIcon(langCode: String) {
        val studyFlag = Config.LANGUAGES[controller.language]?.get("flag") as String
        val nativeFlag = Config.LANGUAGES[controller.nativeLanguage]?.get("flag") as String
        modeLabel.text = "$studyFlag → $nativeFlag"
    }

    fun getView(): LinearLayout = view
}

 */