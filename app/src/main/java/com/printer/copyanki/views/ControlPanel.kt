package com.printer.copyanki.views

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.printer.copyanki.MainActivity
import com.printer.copyanki.R
import com.printer.copyanki.config.Config

class ControlPanel(
    private val context: Context,
    private val controller: MainActivity  // Интерфейс вместо конкретного класса
) {

    private lateinit var view: LinearLayout
    private lateinit var buttonsContainer: LinearLayout
    private lateinit var controlScrollView: ScrollView
    private val buttonActive = mutableMapOf<String, Boolean>()

    init {
        setupView()
        createActionButtons()

        // Скрываем полосу прокрутки
        controlScrollView.isVerticalScrollBarEnabled = false
        controlScrollView.isHorizontalScrollBarEnabled = false
    }

    private fun setupView() {
        view = LayoutInflater.from(context).inflate(R.layout.control_panel, null) as LinearLayout
        controlScrollView = view.findViewById(R.id.controlScrollView)
        buttonsContainer = view.findViewById(R.id.controlButtonsContainer)
    }

    private fun createActionButtons() {
        val actions = listOf(
            ActionButton("➕", "Добавить слово", Config.COLORS["primary"]!!) { controller.addWordDialog() },
            ActionButton("📖", "Словарь", Config.COLORS["accent"]!!) { controller.showVocabulary() },
            ActionButton("⚙️", "Настройки", Config.COLORS["warning"]!!) { controller.showSettingsDialog() },
            ActionButton("🔄", "Обновить", Config.COLORS["success"]!!) { controller.refreshWords() },
            ActionButton("🎓", "Метод", Config.COLORS["secondary"]!!) { controller.showLearningMethod() },
            ActionButton("🎯", "Сложные", Config.COLORS["warning"]!!) { controller.showHardWords() },
            ActionButton("📊", "Статистика", Config.COLORS["text_secondary"]!!) { controller.showDetailedStats() },
            ActionButton("⚡", "Быстрая", Config.COLORS["danger"]!!) { controller.quickTraining() },
            ActionButton("🌐", "Язык", Config.COLORS["primary"]!!) { controller.changeLanguageDialog() }
        )

        for ((index, action) in actions.withIndex()) {
            // Эмодзи + подпись в одной строке
            val rowLayout = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER
                setPadding(0, 8, 0, 4)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Эмодзи
            TextView(context).apply {
                text = action.icon
                textSize = 18f
                setTextColor(Color.parseColor(action.color))
            }.also { rowLayout.addView(it) }

            // Подпись
            TextView(context).apply {
                text = action.label
                textSize = 10f
                setTextColor(Color.parseColor(Config.COLORS["text_secondary"]))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(6, 0, 0, 0)
                }
            }.also { rowLayout.addView(it) }

            buttonsContainer.addView(rowLayout)

            // Кнопка (невидимая, но кликабельная)
            val btn = createActionButton(action.command, "btn_$index")
            buttonsContainer.addView(btn)

            // Разделитель
            if (index < actions.size - 1) {
                val divider = View(context)
                divider.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
                )
                divider.setBackgroundColor(Color.parseColor(Config.COLORS["border"]))
                divider.setPadding(16, 4, 16, 4)
                buttonsContainer.addView(divider)
            }
        }
    }

    private fun createActionButton(command: () -> Unit, btnId: String): Button {
        return Button(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                40
            )
            setBackgroundColor(Color.parseColor(Config.COLORS["bg_card"]))
            setTextColor(Color.parseColor(Config.COLORS["text"]))
            text = ""  // Пустой текст, так как эмодзи и подпись уже есть

            setOnClickListener {
                if (buttonActive[btnId] == true) {
                    return@setOnClickListener
                }

                buttonActive[btnId] = true

                try {
                    command()
                } finally {
                    Handler(Looper.getMainLooper()).postDelayed({
                        buttonActive[btnId] = false
                    }, 500)
                }
            }

            // Эффект при нажатии
            setOnTouchListener { v, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        setBackgroundColor(Color.parseColor(Config.COLORS["primary"]))
                    }
                    android.view.MotionEvent.ACTION_UP,
                    android.view.MotionEvent.ACTION_CANCEL -> {
                        setBackgroundColor(Color.parseColor(Config.COLORS["bg_card"]))
                        performClick()
                    }
                }
                true
            }
        }
    }

    fun getView(): LinearLayout = view

    private data class ActionButton(
        val icon: String,
        val label: String,
        val color: String,
        val command: () -> Unit
    )
}