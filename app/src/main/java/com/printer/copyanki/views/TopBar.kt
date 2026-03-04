/*
package com.printer.copyanki.views

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.printer.copyanki.R
import com.printer.copyanki.config.Config

class TopBar(
    private val context: Context,
    private val controller: MainWindow
) {

    private var fullscreen = false
    private lateinit var view: LinearLayout
    private lateinit var fullscreenBtn: Button
    private lateinit var statsLabel: TextView
    private lateinit var modeFrame: LinearLayout
    private val modeButtons = mutableMapOf<String, Button>()

    init {
        setupView()
    }

    private fun setupView() {
        view = LayoutInflater.from(context).inflate(R.layout.top_bar, null) as LinearLayout

        fullscreenBtn = view.findViewById(R.id.fullscreenButton)
        statsLabel = view.findViewById(R.id.statsLabel)
        modeFrame = view.findViewById(R.id.modeFrame)

        fullscreenBtn.setOnClickListener {
            toggleFullscreen()
        }

        createModeButtons()
    }

    private fun toggleFullscreen() {
        fullscreen = !fullscreen
        fullscreenBtn.text = if (fullscreen) "🗗" else "⛶"
    }

    private fun createModeButtons() {
        val studyLang = controller.language
        val nativeLang = controller.nativeLanguage

        modeFrame.removeAllViews()
        modeButtons.clear()

        val modes = listOf(
            mapOf(
                "code" to "$studyLang-$nativeLang",
                "label" to "${Config.LANGUAGES[studyLang]?.get("flag")}→${Config.LANGUAGES[nativeLang]?.get("flag")}"
            ),
            mapOf(
                "code" to "$nativeLang-$studyLang",
                "label" to "${Config.LANGUAGES[nativeLang]?.get("flag")}→${Config.LANGUAGES[studyLang]?.get("flag")}"
            )
        )

        for (mode in modes) {
            val btn = Button(context).apply {
                text = mode["label"] as String
                textSize = 12f
                setBackgroundColor(Color.parseColor(Config.COLORS["bg_card"]))
                setTextColor(Color.parseColor(Config.COLORS["text_secondary"]))
                setPadding(8, 4, 8, 4)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    36
                ).apply {
                    setMargins(0, 0, 4, 0)
                }

                setOnClickListener {
                    controller.changeMode(mode["code"] as String)
                }
            }
            modeFrame.addView(btn)
            modeButtons[mode["code"] as String] = btn
        }

        updateModeButtons(controller.mode)
    }

    fun updateModeButtons(activeMode: String) {
        for ((modeCode, button) in modeButtons) {
            if (modeCode == activeMode) {
                button.setBackgroundColor(Color.parseColor(Config.COLORS["primary"]))
                button.setTextColor(Color.parseColor(Config.COLORS["text"]))
            } else {
                button.setBackgroundColor(Color.parseColor(Config.COLORS["bg_card"]))
                button.setTextColor(Color.parseColor(Config.COLORS["text_secondary"]))
            }
        }
    }

    fun updateStats(stats: Map<String, Any>) {
        val totalWords = stats["total_words"] as Int
        val learnedWords = stats["learned_words"] as Int
        val progress = stats["progress"] as Double

        statsLabel.text = "$totalWords|$learnedWords|${progress.toInt()}%"
    }

    fun refreshModeButtons() {
        createModeButtons()
    }

    fun getView(): LinearLayout = view
}

 */