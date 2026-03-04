package com.printer.copyanki.views

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.printer.copyanki.R
import com.printer.copyanki.config.Config

class StatsPanel(
    private val context: Context,
    private val controller: Any
) {

    private lateinit var view: LinearLayout
    private lateinit var statsContainer: LinearLayout
    private lateinit var statsScrollView: ScrollView
    private val statValues = mutableMapOf<String, TextView>()
    private lateinit var progressFill: View
    private lateinit var progressText: TextView
    private lateinit var progressBarContainer: FrameLayout

    init {
        setupView()
        createStatsContent()

        // Скрываем полосу прокрутки
        statsScrollView.isVerticalScrollBarEnabled = false
        statsScrollView.isHorizontalScrollBarEnabled = false
    }

    private fun setupView() {
        view = LayoutInflater.from(context).inflate(R.layout.stats_panel, null) as LinearLayout
        statsScrollView = view.findViewById(R.id.statsScrollView)
        statsContainer = view.findViewById(R.id.statsContainer)
    }

    private fun createStatsContent() {
        // ВСЕГО СЛОВ (вверху)

        // Небольшой отступ сверху
        addSpacer(8)

        // Эмодзи + подпись "всего слов"
        addStatRow("📚", "всего слов", "total")

        // Средний промежуток
        addSpacer(10)

        // Значение 0
        addStatValue("total")

        // Средний промежуток
        addSpacer(16)

        // ИЗУЧЕНО

        // Эмодзи + подпись "изучено"
        addStatRow("🎓", "изучено", "learned")

        // Средний промежуток
        addSpacer(10)

        // Значение 0
        addStatValue("learned")

        // Средний промежуток
        addSpacer(16)

        // СЛОЖНЫЕ

        // Эмодзи + подпись "сложные"
        addStatRow("🎯", "сложные", "hard")

        // Средний промежуток
        addSpacer(10)

        // Значение 0
        addStatValue("hard")

        // Средний промежуток
        addSpacer(16)

        // СЕГОДНЯ

        // Эмодзи + подпись "сегодня"
        addStatRow("📅", "сегодня", "today")

        // Средний промежуток
        addSpacer(10)

        // Значение 0
        addStatValue("today")

        // Большой промежуток перед прогрессом
        addSpacer(24)

        // ПРОГРЕСС (в самом низу)

        // Эмодзи прогресс
        addProgressIconRow()

        // Средний промежуток
        addSpacer(10)

        // Панель прогресса
        addProgressBar()

        // Средний промежуток
        addSpacer(10)

        // Текст 0%
        addProgressText()

        // Нижний отступ
        addSpacer(8)
    }

    private fun addSpacer(height: Int) {
        val spacer = View(context)
        spacer.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            height
        )
        statsContainer.addView(spacer)
    }

    private fun addProgressIconRow() {
        val rowLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Эмодзи прогресса
        TextView(context).apply {
            text = "📈"
            textSize = 18f
            setTextColor(Color.parseColor(Config.COLORS["text"]))
        }.also { rowLayout.addView(it) }

        // Подпись "прогресс"
        TextView(context).apply {
            text = "прогресс"
            textSize = 10f
            setTextColor(Color.parseColor(Config.COLORS["text_secondary"]))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(6, 0, 0, 0)
            }
        }.also { rowLayout.addView(it) }

        statsContainer.addView(rowLayout)
    }

    private fun addProgressBar() {
        progressBarContainer = FrameLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                36
            )
            setPadding(8, 4, 8, 4)
        }

        // Фон прогресс-бара
        val backgroundBar = View(context)
        backgroundBar.setBackgroundColor(Color.parseColor(Config.COLORS["bg_dark"]))
        backgroundBar.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            14
        )
        progressBarContainer.addView(backgroundBar)

        // Заполнение прогресс-бара
        progressFill = View(context)
        progressFill.setBackgroundColor(Color.parseColor(Config.COLORS["primary"]))
        progressFill.layoutParams = FrameLayout.LayoutParams(0, 14)
        progressBarContainer.addView(progressFill)

        statsContainer.addView(progressBarContainer)
    }

    private fun addProgressText() {
        progressText = TextView(context).apply {
            text = "0%"
            textSize = 22f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(Color.parseColor(Config.COLORS["text"]))
            gravity = android.view.Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        statsContainer.addView(progressText)
    }

    private fun addStatRow(icon: String, label: String, key: String) {
        val rowLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Эмодзи
        TextView(context).apply {
            text = icon
            textSize = 18f
            setTextColor(Color.parseColor(Config.COLORS["text"]))
        }.also { rowLayout.addView(it) }

        // Подпись
        TextView(context).apply {
            text = label
            textSize = 10f
            setTextColor(Color.parseColor(Config.COLORS["text_secondary"]))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(6, 0, 0, 0)
            }
        }.also { rowLayout.addView(it) }

        statsContainer.addView(rowLayout)
    }

    private fun addStatValue(key: String) {
        val valueView = TextView(context).apply {
            text = "0"
            textSize = 22f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(Color.parseColor(Config.COLORS["text"]))
            gravity = android.view.Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        statsContainer.addView(valueView)
        statValues[key] = valueView
    }

    fun updateStats(stats: Map<String, Any>) {
        val totalWords = stats["total_words"] as Int
        val learnedWords = stats["learned_words"] as Int
        val hardWords = stats["hard_words"] as Int
        val dailyWords = stats["daily_words"] as Int
        val progress = stats["progress"] as Double

        statValues["total"]?.text = formatNumber(totalWords)
        statValues["learned"]?.text = formatNumber(learnedWords)
        statValues["hard"]?.text = formatNumber(hardWords)
        statValues["today"]?.text = formatNumber(dailyWords)

        progressText.text = "${progress.toInt()}%"

        // Обновляем ширину заполнения прогресс-бара
        progressBarContainer.post {
            val width = progressBarContainer.width - 16
            if (width > 0) {
                val fillWidth = (width * (progress / 100)).toInt()
                progressFill.layoutParams.width = fillWidth
                progressFill.requestLayout()
            }
        }
    }

    private fun formatNumber(num: Int): String {
        return when {
            num >= 1000 -> "${num/1000}k"
            else -> num.toString()
        }
    }

    fun getView(): LinearLayout = view
}