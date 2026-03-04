/*
package com.printer.copyanki.views

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.printer.copyanki.R
import com.printer.copyanki.config.Config

class BottomBar(
    private val context: Context,
    private val controller: MainWindow
) {

    private lateinit var view: LinearLayout
    private lateinit var progressLabel: TextView
    private lateinit var dailyCounterLabel: TextView
    private lateinit var speechStatusLabel: TextView
    private lateinit var versionLabel: TextView

    init {
        setupView()
    }

    private fun setupView() {
        view = LayoutInflater.from(context).inflate(R.layout.bottom_bar, null) as LinearLayout

        progressLabel = view.findViewById(R.id.progressLabel)
        dailyCounterLabel = view.findViewById(R.id.dailyCounterLabel)
        speechStatusLabel = view.findViewById(R.id.speechStatusLabel)
        versionLabel = view.findViewById(R.id.versionLabel)

        // Берем версию из конфига
        versionLabel.text = Config.TEXTS["version"] as String
    }

    fun updateStats(stats: Map<String, Any>) {
        val progress = stats["progress"] as Double
        val dailyWords = stats["daily_words"] as Int
        val enabled = controller.settings["enabled"] as Boolean

        progressLabel.text = "${progress.toInt()}%"
        dailyCounterLabel.text = "•$dailyWords"
        speechStatusLabel.text = if (enabled) "🔊" else "🔇"
    }

    fun getView(): LinearLayout = view
}

 */