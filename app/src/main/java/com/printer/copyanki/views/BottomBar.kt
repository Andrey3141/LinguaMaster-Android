package com.printer.copyanki.views

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.printer.copyanki.MainActivity
import com.printer.copyanki.R
import com.printer.copyanki.config.Config

class BottomBar(
    private val context: Context,
    private val controller: MainActivity
) {
    private lateinit var view: LinearLayout
    private lateinit var speechStatusLabel: TextView
    private lateinit var versionLabel: TextView

    init {
        setupView()
    }

    private fun setupView() {
        view = LayoutInflater.from(context).inflate(R.layout.bottom_bar, null) as LinearLayout

        speechStatusLabel = view.findViewById(R.id.speechStatusLabel)
        versionLabel = view.findViewById(R.id.versionLabel)

        // Загружаем ВСЕ данные из Config.TEXTS
        val year = Config.TEXTS["year"] as? String ?: "2026"
        val appName = Config.TEXTS["app_name"] as? String ?: "LinguaMaster"
        val version = Config.TEXTS["version"] as? String ?: "v1.0"
        versionLabel.text = "© $year $appName $version"

        // Обновляем статус озвучки
        updateSpeechStatus()
    }

    fun updateSpeechStatus() {
        speechStatusLabel.text = if (controller.isSpeechEnabled()) "🔊" else "🔇"
    }

    fun getView(): LinearLayout = view
}