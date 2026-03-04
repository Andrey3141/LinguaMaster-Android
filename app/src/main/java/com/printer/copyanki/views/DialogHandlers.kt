/*
package com.printer.copyanki.views

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.printer.copyanki.R
import com.printer.copyanki.config.Config
import com.printer.copyanki.utils.Notifications
import com.printer.copyanki.utils.speechSynth

object DialogHandlers {

    fun addWordDialog(controller: MainWindow) {
        val context = controller.getAppContext()

        val dialog = AlertDialog.Builder(context)
            .setTitle("➕ Добавить новое слово")
            .setView(R.layout.dialog_add_word)
            .setCancelable(false)
            .create()

        dialog.show()

        val langInfo = dialog.findViewById<TextView>(R.id.lang_info)
        val foreignEntry = dialog.findViewById<EditText>(R.id.foreign_entry)
        val translationEntry = dialog.findViewById<EditText>(R.id.translation_entry)
        val categorySpinner = dialog.findViewById<Spinner>(R.id.category_spinner)
        val addButton = dialog.findViewById<Button>(R.id.add_button)
        val cancelButton = dialog.findViewById<Button>(R.id.cancel_button)

        langInfo?.text = "${Config.LANGUAGES[controller.language]?.get("flag")} ${Config.LANGUAGES[controller.language]?.get("name")} → " +
                "${Config.LANGUAGES[controller.nativeLanguage]?.get("flag")} ${Config.LANGUAGES[controller.nativeLanguage]?.get("name")}"

        val categories = mutableListOf<String>().apply {
            addAll(controller.model.getAllCategories())
            if (!contains("Основные")) add(0, "Основные")
        }

        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner?.adapter = adapter

        addButton?.setOnClickListener {
            val foreign = foreignEntry?.text.toString().trim()
            val translation = translationEntry?.text.toString().trim()
            val category = if (categorySpinner?.selectedItem != null) {
                categorySpinner.selectedItem.toString()
            } else {
                "Основные"
            }

            if (foreign.isEmpty() || translation.isEmpty()) {
                Notifications.showNotification(
                    context as android.app.Activity,
                    "Ошибка",
                    "Заполните оба поля",
                    "error"
                )
                return@setOnClickListener
            }

            val success = controller.addWord(foreign, translation, category)

            if (success) {
                Notifications.showNotification(
                    context as android.app.Activity,
                    "Успех",
                    "Слово добавлено",
                    "success"
                )
                dialog.dismiss()
                controller.nextWord()
            } else {
                Notifications.showNotification(
                    context as android.app.Activity,
                    "Ошибка",
                    "Такое слово уже существует",
                    "error"
                )
            }
        }

        cancelButton?.setOnClickListener {
            dialog.dismiss()
        }

        foreignEntry?.requestFocus()
    }

    fun showSettingsDialog(controller: MainWindow) {
        val context = controller.getAppContext()

        val dialog = AlertDialog.Builder(context)
            .setTitle("⚙️ Настройки")
            .setView(R.layout.dialog_settings)
            .setCancelable(false)
            .create()

        dialog.show()

        val autoCheck = dialog.findViewById<CheckBox>(R.id.auto_advance_check)
        val hintsCheck = dialog.findViewById<CheckBox>(R.id.show_hints_check)
        val hintSlider = dialog.findViewById<SeekBar>(R.id.hint_threshold_slider)
        val hintValue = dialog.findViewById<TextView>(R.id.hint_threshold_value)
        val hardSlider = dialog.findViewById<SeekBar>(R.id.hard_threshold_slider)
        val speechCheck = dialog.findViewById<CheckBox>(R.id.speech_enabled_check)
        val autoSpeakCheck = dialog.findViewById<CheckBox>(R.id.auto_speak_check)
        val volumeSlider = dialog.findViewById<SeekBar>(R.id.volume_slider)
        val volumeValue = dialog.findViewById<TextView>(R.id.volume_value)
        val speedSlider = dialog.findViewById<SeekBar>(R.id.speed_slider)
        val speedValue = dialog.findViewById<TextView>(R.id.speed_value)
        val voiceButton = dialog.findViewById<Button>(R.id.voice_settings_button)
        val saveButton = dialog.findViewById<Button>(R.id.save_button)

        autoCheck?.isChecked = controller.settings["auto_advance"] as Boolean

        hintsCheck?.isChecked = controller.settings["show_hints"] as Boolean
        val threshold = controller.settings["hint_threshold"] as Int
        hintSlider?.progress = threshold - 1
        hintValue?.text = threshold.toString()

        hintSlider?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = progress + 1
                hintValue?.text = value.toString()
                controller.settings["hint_threshold"] = value
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        hintsCheck?.setOnCheckedChangeListener { _, isChecked ->
            controller.settings["show_hints"] = isChecked
            hintSlider?.isEnabled = isChecked
        }

        hardSlider?.progress = controller.settings["hard_word_threshold"] as Int
        hardSlider?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                controller.settings["hard_word_threshold"] = progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        speechCheck?.isChecked = speechSynth.enabled
        speechCheck?.setOnCheckedChangeListener { _, isChecked ->
            speechSynth.enabled = isChecked
            controller.settings["enabled"] = isChecked
        }

        autoSpeakCheck?.isChecked = controller.settings["auto_speak"] as Boolean
        autoSpeakCheck?.setOnCheckedChangeListener { _, isChecked ->
            controller.settings["auto_speak"] = isChecked
        }

        volumeSlider?.progress = speechSynth.volume
        volumeValue?.text = "${speechSynth.volume}%"
        volumeSlider?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                volumeValue?.text = "$progress%"
                speechSynth.volume = progress
                controller.settings["volume"] = progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        speedSlider?.progress = (speechSynth.speed * 100).toInt()
        speedValue?.text = "${speechSynth.speed}x"
        speedSlider?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val speed = progress / 100f
                speedValue?.text = "${speed}x"
                speechSynth.speed = speed
                controller.settings["speed"] = speed
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        voiceButton?.setOnClickListener {
            VoiceSettingsDialog.showDialog(controller)
        }

        saveButton?.setOnClickListener {
            controller.applySettings()
            dialog.dismiss()
        }
    }

    fun changeLanguageDialog(controller: MainWindow) {
        val context = controller.getAppContext()

        val dialog = AlertDialog.Builder(context)
            .setTitle("🌐 Выбор языков")
            .setView(R.layout.dialog_language)
            .setCancelable(false)
            .create()

        dialog.show()

        val leftList = dialog.findViewById<ListView>(R.id.language_list_left)
        val rightList = dialog.findViewById<ListView>(R.id.language_list_right)
        val leftTitle = dialog.findViewById<TextView>(R.id.left_title)
        val rightTitle = dialog.findViewById<TextView>(R.id.right_title)
        val saveButton = dialog.findViewById<Button>(R.id.save_button)
        val applyButton = dialog.findViewById<Button>(R.id.apply_button)
        val cancelButton = dialog.findViewById<Button>(R.id.cancel_button)

        leftTitle?.text = "Изучаемый язык:"
        rightTitle?.text = "Родной язык:"

        val languageItems = Config.LANGUAGES.map { (code, info) ->
            LanguageItem(code, "${info["flag"]} ${info["name"]}")
        }

        val leftAdapter = LanguageAdapter(context, languageItems, controller.language)
        val rightAdapter = LanguageAdapter(context, languageItems, controller.nativeLanguage)

        leftList?.adapter = leftAdapter
        rightList?.adapter = rightAdapter

        leftList?.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            leftAdapter.setSelectedPosition(position)
            leftAdapter.notifyDataSetChanged()
        }

        rightList?.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            rightAdapter.setSelectedPosition(position)
            rightAdapter.notifyDataSetChanged()
        }

        fun validateAndApply(): Boolean {
            val studyLang = leftAdapter.getSelectedCode()
            val nativeLang = rightAdapter.getSelectedCode()

            if (studyLang == nativeLang) {
                Notifications.showNotification(
                    context as android.app.Activity,
                    "Ошибка выбора языка",
                    "Изучаемый и родной языки не могут быть одинаковыми!",
                    "error"
                )
                return false
            }
            return true
        }

        fun applyLanguages() {
            if (!validateAndApply()) return

            val studyLang = leftAdapter.getSelectedCode()
            val nativeLang = rightAdapter.getSelectedCode()

            controller.language = studyLang
            controller.nativeLanguage = nativeLang
            controller.mode = "$studyLang-$nativeLang"

            controller.topBar.refreshModeButtons()

            when (val panel = controller.currentPanel) {
                is TrainingPanel -> panel.updateModeIcon(studyLang)
                is TestPanel -> panel.updateModeIcon(studyLang)
                is MatchPanel -> panel.updateModeIcon(studyLang)
            }

            val wordsForMode = controller.model.getWordsForMode(controller.mode)

            if (wordsForMode.isEmpty()) {
                Notifications.showNotification(
                    context as android.app.Activity,
                    "Словарь пуст",
                    "Для языка ${Config.LANGUAGES[studyLang]?.get("name")} ↔ ${Config.LANGUAGES[nativeLang]?.get("name")} нет слов",
                    "warning"
                )
            } else {
                Notifications.showNotification(
                    context as android.app.Activity,
                    "Язык изменен",
                    "Режим: ${Config.LANGUAGES[studyLang]?.get("flag")} → ${Config.LANGUAGES[nativeLang]?.get("flag")}",
                    "success"
                )
            }

            controller.nextWord()
        }

        saveButton?.setOnClickListener {
            applyLanguages()
            dialog.dismiss()
        }

        applyButton?.setOnClickListener {
            applyLanguages()
        }

        cancelButton?.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun showLearningMethodDialog(controller: MainWindow) {
        val context = controller.getAppContext()

        val dialog = AlertDialog.Builder(context)
            .setTitle("Метод обучения")
            .setView(R.layout.dialog_learning_method)
            .setCancelable(true)
            .create()

        dialog.show()

        val manualBtn = dialog.findViewById<Button>(R.id.method_manual)
        val testBtn = dialog.findViewById<Button>(R.id.method_test)
        val matchBtn = dialog.findViewById<Button>(R.id.method_match)

        manualBtn?.setOnClickListener {
            controller.changeLearningMethod("manual")
            dialog.dismiss()
        }

        testBtn?.setOnClickListener {
            controller.changeLearningMethod("test")
            dialog.dismiss()
        }

        matchBtn?.setOnClickListener {
            controller.changeLearningMethod("match")
            dialog.dismiss()
        }
    }

    private data class LanguageItem(val code: String, val displayName: String)

    private class LanguageAdapter(
        context: Context,
        private val items: List<LanguageItem>,
        private val defaultCode: String
    ) : ArrayAdapter<LanguageItem>(context, android.R.layout.simple_list_item_single_choice, items) {

        private var selectedPosition = items.indexOfFirst { it.code == defaultCode }.coerceAtLeast(0)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(
                android.R.layout.simple_list_item_single_choice,
                parent,
                false
            )

            val textView = view.findViewById<TextView>(android.R.id.text1)
            textView.text = items[position].displayName
            textView.setTextColor(android.graphics.Color.parseColor(Config.COLORS["text"]))

            return view
        }

        fun setSelectedPosition(position: Int) {
            selectedPosition = position
        }

        fun getSelectedCode(): String = items[selectedPosition].code
    }
}

 */