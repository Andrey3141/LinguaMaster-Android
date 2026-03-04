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
import java.util.*

class MatchPanel(
    private val context: Context,
    private val controller: MainWindow
) {

    private lateinit var mainFrame: LinearLayout
    private lateinit var modeLabel: TextView
    private lateinit var hintLabel: TextView
    private lateinit var columnsFrame: LinearLayout
    private lateinit var leftFrame: LinearLayout
    private lateinit var rightFrame: LinearLayout
    private lateinit var leftButtonsFrame: LinearLayout
    private lateinit var rightButtonsFrame: LinearLayout
    private lateinit var progressLabel: TextView
    private lateinit var checkButton: Button

    private var wordsData = listOf<Map<String, Any>>()
    private val leftButtons = mutableListOf<Button>()
    private val rightButtons = mutableListOf<Button>()
    private var leftWords = mutableListOf<String>()
    private var rightWords = mutableListOf<String>()
    private val matchedPairs = mutableListOf<Pair<Int, Int>>()
    private var selectedLeft: Int? = null
    private var selectedRight: Int? = null
    private val correctMapping = mutableMapOf<Int, String>()

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
            text = "$studyFlag ↔ $nativeFlag"
            textSize = 28f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(Color.parseColor(Config.COLORS["text"]))
        }
        modeFrame.addView(modeLabel)

        hintLabel = TextView(context).apply {
            text = "Сопоставьте слова, нажимая на пары"
            textSize = 16f
            setTextColor(Color.parseColor(Config.COLORS["text_secondary"]))
            setPadding(0, 20, 0, 20)
        }
        mainFrame.addView(hintLabel)

        columnsFrame = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Color.parseColor(Config.COLORS["bg_dark"]))
            setPadding(40, 40, 40, 40)
        }
        mainFrame.addView(columnsFrame)

        leftFrame = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(Config.COLORS["bg_dark"]))
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
        }
        columnsFrame.addView(leftFrame)

        val studyLangName = Config.LANGUAGES[controller.language]?.get("name") as String
        TextView(context).apply {
            text = "$studyLangName:"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(Color.parseColor(Config.COLORS["primary"]))
            setPadding(0, 0, 0, 20)
        }.also { leftFrame.addView(it) }

        leftButtonsFrame = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(Config.COLORS["bg_dark"]))
        }
        leftFrame.addView(leftButtonsFrame)

        rightFrame = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(Config.COLORS["bg_dark"]))
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
        }
        columnsFrame.addView(rightFrame)

        val nativeLangName = Config.LANGUAGES[controller.nativeLanguage]?.get("name") as String
        TextView(context).apply {
            text = "$nativeLangName:"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(Color.parseColor(Config.COLORS["accent"]))
            setPadding(0, 0, 0, 20)
        }.also { rightFrame.addView(it) }

        rightButtonsFrame = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(Config.COLORS["bg_dark"]))
        }
        rightFrame.addView(rightButtonsFrame)

        progressLabel = TextView(context).apply {
            text = "Сопоставлено: 0/3"
            textSize = 16f
            setTextColor(Color.parseColor(Config.COLORS["text"]))
            setPadding(0, 20, 0, 20)
        }
        mainFrame.addView(progressLabel)

        checkButton = Button(context).apply {
            text = "✅ Проверить все"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setBackgroundColor(Color.parseColor(Config.COLORS["primary"]))
            setTextColor(Color.parseColor(Config.COLORS["text"]))
            setPadding(60, 24, 60, 24)

            setOnClickListener {
                checkAllMatches()
            }
        }
        mainFrame.addView(checkButton)
    }

    fun setQuestion(wordsData: List<Map<String, Any>>) {
        this.wordsData = wordsData
        matchedPairs.clear()
        selectedLeft = null
        selectedRight = null
        leftButtons.clear()
        rightButtons.clear()
        leftWords.clear()
        rightWords.clear()
        correctMapping.clear()

        leftButtonsFrame.removeAllViews()
        rightButtonsFrame.removeAllViews()

        val studyLang = controller.language
        val nativeLang = controller.nativeLanguage

        val shuffledData = wordsData.shuffled()

        for (wordData in shuffledData) {
            if (wordData["language"] == studyLang && wordData["native_language"] == nativeLang) {
                leftWords.add(wordData["foreign"] as String)
                rightWords.add(wordData["translation"] as String)
            } else {
                leftWords.add(wordData["translation"] as String)
                rightWords.add(wordData["foreign"] as String)
            }
        }

        rightWords = rightWords.shuffled().toMutableList()

        for (i in leftWords.indices) {
            val leftWord = leftWords[i]
            for (wordData in wordsData) {
                if (wordData["language"] == studyLang && wordData["native_language"] == nativeLang) {
                    if (leftWord == wordData["foreign"]) {
                        correctMapping[i] = wordData["translation"] as String
                        break
                    } else if (leftWord == wordData["translation"]) {
                        correctMapping[i] = wordData["foreign"] as String
                        break
                    }
                } else {
                    if (leftWord == wordData["translation"]) {
                        correctMapping[i] = wordData["foreign"] as String
                        break
                    } else if (leftWord == wordData["foreign"]) {
                        correctMapping[i] = wordData["translation"] as String
                        break
                    }
                }
            }
        }

        for (i in leftWords.indices) {
            val btn = createLeftButton(i)
            leftButtonsFrame.addView(btn)
            leftButtons.add(btn)
        }

        for (i in rightWords.indices) {
            val btn = createRightButton(i)
            rightButtonsFrame.addView(btn)
            rightButtons.add(btn)
        }

        updateProgress()
    }

    private fun createLeftButton(index: Int): Button {
        return Button(context).apply {
            text = leftWords[index]
            textSize = 20f
            setBackgroundColor(Color.parseColor(Config.COLORS["bg_card"]))
            setTextColor(Color.parseColor(Config.COLORS["text"]))
            setPadding(30, 24, 30, 24)

            setOnClickListener {
                selectLeft(index)
            }
        }
    }

    private fun createRightButton(index: Int): Button {
        return Button(context).apply {
            text = rightWords[index]
            textSize = 20f
            setBackgroundColor(Color.parseColor(Config.COLORS["bg_card"]))
            setTextColor(Color.parseColor(Config.COLORS["text"]))
            setPadding(30, 24, 30, 24)

            setOnClickListener {
                selectRight(index)
            }
        }
    }

    private fun selectLeft(index: Int) {
        for ((leftIdx, _) in matchedPairs) {
            if (leftIdx == index) return
        }

        selectedLeft?.let {
            leftButtons[it].setBackgroundColor(Color.parseColor(Config.COLORS["bg_card"]))
        }

        selectedLeft = index
        leftButtons[index].setBackgroundColor(Color.parseColor(Config.COLORS["primary"]))

        if (selectedRight != null) {
            makeMatch()
        }
    }

    private fun selectRight(index: Int) {
        for ((_, rightIdx) in matchedPairs) {
            if (rightIdx == index) return
        }

        selectedRight?.let {
            rightButtons[it].setBackgroundColor(Color.parseColor(Config.COLORS["bg_card"]))
        }

        selectedRight = index
        rightButtons[index].setBackgroundColor(Color.parseColor(Config.COLORS["accent"]))

        if (selectedLeft != null) {
            makeMatch()
        }
    }

    private fun makeMatch() {
        val leftIdx = selectedLeft!!
        val rightIdx = selectedRight!!

        val leftWord = leftWords[leftIdx]
        val rightWord = rightWords[rightIdx]

        val isCorrect = (correctMapping[leftIdx] == rightWord)

        if (isCorrect) {
            matchedPairs.add(Pair(leftIdx, rightIdx))

            leftButtons[leftIdx].isEnabled = false
            leftButtons[leftIdx].setBackgroundColor(Color.parseColor(Config.COLORS["success"]))
            rightButtons[rightIdx].isEnabled = false
            rightButtons[rightIdx].setBackgroundColor(Color.parseColor(Config.COLORS["success"]))

            selectedLeft = null
            selectedRight = null

            updateProgress()

            if (matchedPairs.size == 3) {
                allMatched()
            }
        } else {
            Animations.animateError(columnsFrame)

            leftButtons[leftIdx].setBackgroundColor(Color.parseColor(Config.COLORS["danger"]))
            rightButtons[rightIdx].setBackgroundColor(Color.parseColor(Config.COLORS["danger"]))

            Handler(Looper.getMainLooper()).postDelayed({
                resetSelection(leftIdx, rightIdx)
            }, 500)
        }
    }

    private fun resetSelection(leftIdx: Int, rightIdx: Int) {
        leftButtons[leftIdx].setBackgroundColor(Color.parseColor(Config.COLORS["bg_card"]))
        rightButtons[rightIdx].setBackgroundColor(Color.parseColor(Config.COLORS["bg_card"]))

        selectedLeft = null
        selectedRight = null
    }

    private fun updateProgress() {
        progressLabel.text = "Сопоставлено: ${matchedPairs.size}/3"
    }

    private fun allMatched() {
        Animations.animateSuccess(columnsFrame)
        progressLabel.text = "✅ Отлично! Все верно!"
        hintLabel.text = "Молодец! Следующее задание..."

        if (controller.settings["auto_advance"] as Boolean) {
            Handler(Looper.getMainLooper()).postDelayed({
                controller.nextWord()
            }, 1500)
        }
    }

    private fun checkAllMatches() {
        if (matchedPairs.size == 3) {
            allMatched()
            return
        }

        for ((leftIdx, rightIdx) in matchedPairs) {
            leftButtons[leftIdx].setBackgroundColor(Color.parseColor(Config.COLORS["success"]))
            rightButtons[rightIdx].setBackgroundColor(Color.parseColor(Config.COLORS["success"]))
        }

        var hasErrors = false
        for (leftIdx in leftWords.indices) {
            if (matchedPairs.any { it.first == leftIdx }) continue

            val correctRight = correctMapping[leftIdx]
            for (rightIdx in rightWords.indices) {
                if (rightWords[rightIdx] == correctRight) {
                    val alreadyMatched = matchedPairs.any { it.second == rightIdx }

                    if (!alreadyMatched) {
                        leftButtons[leftIdx].setBackgroundColor(Color.parseColor(Config.COLORS["warning"]))
                        rightButtons[rightIdx].setBackgroundColor(Color.parseColor(Config.COLORS["warning"]))
                        hasErrors = true
                    }
                    break
                }
            }
        }

        if (hasErrors) {
            hintLabel.text = "Желтым подсвечены правильные пары, которые еще не сопоставлены"
        } else {
            hintLabel.text = "Сопоставьте оставшиеся слова"
        }
    }

    fun showMessage(message: String) {
        leftButtonsFrame.removeAllViews()
        rightButtonsFrame.removeAllViews()
        hintLabel.text = message
        progressLabel.text = ""
        checkButton.isEnabled = false
    }

    fun showNoWordsMessage() {
        showMessage("Недостаточно слов для режима соотношения. Нужно минимум 3 слова.")
    }

    fun updateModeIcon(langCode: String) {
        val studyFlag = Config.LANGUAGES[controller.language]?.get("flag") as String
        val nativeFlag = Config.LANGUAGES[controller.nativeLanguage]?.get("flag") as String
        modeLabel.text = "$studyFlag ↔ $nativeFlag"

        val studyLangName = Config.LANGUAGES[controller.language]?.get("name") as String
        val nativeLangName = Config.LANGUAGES[controller.nativeLanguage]?.get("name") as String

        for (i in 0 until leftFrame.childCount) {
            val child = leftFrame.getChildAt(i)
            if (child is TextView && child.currentTextColor == Color.parseColor(Config.COLORS["primary"])) {
                child.text = "$studyLangName:"
            }
        }

        for (i in 0 until rightFrame.childCount) {
            val child = rightFrame.getChildAt(i)
            if (child is TextView && child.currentTextColor == Color.parseColor(Config.COLORS["accent"])) {
                child.text = "$nativeLangName:"
            }
        }
    }

    fun getView(): LinearLayout = mainFrame
}

 */