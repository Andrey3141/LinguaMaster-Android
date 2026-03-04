/*
package com.printer.copyanki.views

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.*
import com.printer.copyanki.R
import com.printer.copyanki.config.Config
import com.printer.copyanki.utils.Notifications
import com.printer.copyanki.utils.speechSynth

object VoiceSettingsDialog {

    fun showDialog(controller: MainWindow) {
        val context = controller.getAppContext()

        val dialog = AlertDialog.Builder(context)
            .setTitle("🎤 Настройка голосов")
            .setView(R.layout.dialog_voice_settings)
            .setCancelable(false)
            .create()

        dialog.show()

        val mainFrame = dialog.findViewById<LinearLayout>(R.id.main_frame)
        val saveButton = dialog.findViewById<Button>(R.id.save_button)
        val cancelButton = dialog.findViewById<Button>(R.id.cancel_button)

        val voiceVars = mutableMapOf<String, String>()

        val testTexts = mapOf(
            "ru" to "Это тестовое предложение",
            "en" to "This is a test sentence",
            "es" to "Frase de prueba",
            "fr" to "Phrase de test",
            "de" to "Testsatz",
            "zh" to "测试句子",
            "ja" to "テスト文",
            "ko" to "테스트 문장",
            "pt" to "Frase de teste",
            "it" to "Frase di prova",
            "ar" to "جملة اختبار"
        )

        for ((langCode, langInfo) in Config.LANGUAGES) {
            val langContainer = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 20, 0, 20)
            }

            val titleView = TextView(context).apply {
                text = "${langInfo["flag"]} ${langInfo["name"]}"
                textSize = 18f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setTextColor(android.graphics.Color.parseColor(Config.COLORS["text"]))
            }
            langContainer.addView(titleView)

            val allVoices = speechSynth.getAllVoicesForLanguage(langCode)
            val defaultVoice = speechSynth.getDefaultVoiceForLanguage(langCode)

            var defaultDisplay: String? = null
            if (defaultVoice.isNotEmpty()) {
                defaultDisplay = allVoices.firstOrNull { it.contains(defaultVoice) } ?: "🌐 $defaultVoice"
            }

            if (allVoices.isEmpty()) {
                TextView(context).apply {
                    text = "❌ Нет голосов"
                    textSize = 14f
                    setTextColor(android.graphics.Color.parseColor(Config.COLORS["danger"]))
                }.also { langContainer.addView(it) }
                mainFrame?.addView(langContainer)
                continue
            }

            val voiceFrame = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            val initialVoice = if (defaultDisplay != null && defaultDisplay in allVoices) {
                defaultDisplay
            } else {
                allVoices[0]
            }
            voiceVars[langCode] = initialVoice

            val voiceSpinner = Spinner(context).apply {
                val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, allVoices)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                this.adapter = adapter

                val position = allVoices.indexOf(initialVoice).coerceAtLeast(0)
                setSelection(position)

                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        voiceVars[langCode] = allVoices[position]
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
            voiceFrame.addView(voiceSpinner)

            val testText = testTexts[langCode] ?: "Test"

            val testButton = Button(context).apply {
                text = "▶"
                textSize = 16f
                setBackgroundColor(android.graphics.Color.parseColor(Config.COLORS["secondary"]))
                setTextColor(android.graphics.Color.parseColor(Config.COLORS["text"]))

                setOnClickListener {
                    val selectedVoice = voiceVars[langCode]
                    if (selectedVoice != null) {
                        speechSynth.testVoice(selectedVoice, testText)
                    }
                }
            }
            voiceFrame.addView(testButton)

            langContainer.addView(voiceFrame)
            mainFrame?.addView(langContainer)
        }

        saveButton?.setOnClickListener {
            for ((langCode, voice) in voiceVars) {
                speechSynth.setVoiceForLanguage(langCode, voice)
            }
            Notifications.showNotification(
                context as android.app.Activity,
                "Настройки сохранены",
                "Выбранные голоса сохранены",
                "success"
            )
            dialog.dismiss()
        }

        cancelButton?.setOnClickListener {
            dialog.dismiss()
        }
    }
}

 */