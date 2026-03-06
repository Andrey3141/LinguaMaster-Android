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

    // Общий прогресс
    private lateinit var overallProgressFill: View
    private lateinit var overallProgressText: TextView
    private lateinit var overallProgressBarContainer: FrameLayout

    // Прогресс дня
    private lateinit var dailyProgressFill: View
    private lateinit var dailyProgressText: TextView
    private lateinit var dailyProgressBarContainer: FrameLayout

    init {
        setupView()
        createStatsContent()

        statsScrollView.isVerticalScrollBarEnabled = false
        statsScrollView.isHorizontalScrollBarEnabled = false
    }

    private fun setupView() {
        view = LayoutInflater.from(context).inflate(R.layout.stats_panel, null) as LinearLayout
        statsScrollView = view.findViewById(R.id.statsScrollView)
        statsContainer = view.findViewById(R.id.statsContainer)
    }

    private fun createStatsContent() {
        // ВСЕГО СЛОВ
        addSpacer(8)
        addStatRow("📚 ", "всего слов", "total")
        addSpacer(10)
        addStatValue("total")
        addSpacer(16)

        // ИЗУЧЕНО
        addStatRow("🎓 ", "изучено", "learned")
        addSpacer(10)
        addStatValue("learned")
        addSpacer(16)

        // СЛОЖНЫЕ
        addStatRow("🎯 ", "сложные", "hard")
        addSpacer(10)
        addStatValue("hard")
        addSpacer(16)

        // СЕГОДНЯ
        addStatRow("📅 ", "сегодня", "today")
        addSpacer(10)
        addStatValue("today")
        addSpacer(24)

        // ОБЩИЙ ПРОГРЕСС (переименовано)
        addProgressIconRow("общий прогресс")
        addSpacer(10)
        addOverallProgressBar()
        addSpacer(10)
        addOverallProgressText()
        addSpacer(16)

        // ПРОГРЕСС ДНЯ (новый)
        addProgressIconRow("прогресс дня")
        addSpacer(10)
        addDailyProgressBar()
        addSpacer(10)
        addDailyProgressText()
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

    private fun addProgressIconRow(label: String) {
        val rowLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        TextView(context).apply {
            text = "📈 "
            textSize = 18f
            setTextColor(Color.parseColor(Config.COLORS["text"]))
        }.also { rowLayout.addView(it) }

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

    private fun addOverallProgressBar() {
        overallProgressBarContainer = FrameLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                36
            )
            setPadding(8, 4, 8, 4)
        }

        val backgroundBar = View(context)
        backgroundBar.setBackgroundColor(Color.parseColor(Config.COLORS["bg_dark"]))
        backgroundBar.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            14
        )
        overallProgressBarContainer.addView(backgroundBar)

        overallProgressFill = View(context)
        overallProgressFill.setBackgroundColor(Color.parseColor(Config.COLORS["primary"]))
        overallProgressFill.layoutParams = FrameLayout.LayoutParams(0, 14)
        overallProgressBarContainer.addView(overallProgressFill)

        statsContainer.addView(overallProgressBarContainer)
    }

    private fun addOverallProgressText() {
        overallProgressText = TextView(context).apply {
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
        statsContainer.addView(overallProgressText)
    }

    private fun addDailyProgressBar() {
        dailyProgressBarContainer = FrameLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                36
            )
            setPadding(8, 4, 8, 4)
        }

        val backgroundBar = View(context)
        backgroundBar.setBackgroundColor(Color.parseColor(Config.COLORS["bg_dark"]))
        backgroundBar.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            14
        )
        dailyProgressBarContainer.addView(backgroundBar)

        dailyProgressFill = View(context)
        dailyProgressFill.setBackgroundColor(Color.parseColor(Config.COLORS["secondary"]))
        dailyProgressFill.layoutParams = FrameLayout.LayoutParams(0, 14)
        dailyProgressBarContainer.addView(dailyProgressFill)

        statsContainer.addView(dailyProgressBarContainer)
    }

    private fun addDailyProgressText() {
        dailyProgressText = TextView(context).apply {
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
        statsContainer.addView(dailyProgressText)
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

        TextView(context).apply {
            text = icon
            textSize = 18f
            setTextColor(Color.parseColor(Config.COLORS["text"]))
        }.also { rowLayout.addView(it) }

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
        val accuracyToday = stats["accuracy_today"] as Double

        statValues["total"]?.text = formatNumber(totalWords)
        statValues["learned"]?.text = formatNumber(learnedWords)
        statValues["hard"]?.text = formatNumber(hardWords)
        statValues["today"]?.text = formatNumber(dailyWords)

        // Общий прогресс
        overallProgressText.text = "${progress.toInt()}%"
        overallProgressBarContainer.post {
            val width = overallProgressBarContainer.width - 16
            if (width > 0) {
                val fillWidth = (width * (progress / 100)).toInt()
                overallProgressFill.layoutParams.width = fillWidth
                overallProgressFill.requestLayout()
            }
        }

        // Прогресс дня (точность)
        dailyProgressText.text = "${accuracyToday.toInt()}%"
        dailyProgressBarContainer.post {
            val width = dailyProgressBarContainer.width - 16
            if (width > 0) {
                val fillWidth = (width * (accuracyToday / 100)).toInt()
                dailyProgressFill.layoutParams.width = fillWidth
                dailyProgressFill.requestLayout()
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