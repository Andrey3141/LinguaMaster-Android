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

object StatsDialog {

    fun showHardWords(controller: MainWindow) {
        val context = controller.getAppContext()
        val hardWords = controller.model.getHardWords()

        if (hardWords.isEmpty()) {
            Notifications.showNotification(
                context as android.app.Activity,
                "Сложные слова",
                "У вас пока нет сложных слов!",
                "info"
            )
            return
        }

        val dialog = AlertDialog.Builder(context)
            .setTitle("🎯 Сложные слова (${hardWords.size})")
            .setView(R.layout.dialog_hard_words)
            .setCancelable(true)
            .create()

        dialog.show()

        val listView = dialog.findViewById<ListView>(R.id.hard_words_list)
        val trainButton = dialog.findViewById<Button>(R.id.train_button)
        val moreText = dialog.findViewById<TextView>(R.id.more_text)

        val adapter = HardWordsAdapter(context, hardWords.take(10))
        listView?.adapter = adapter

        if (hardWords.size > 10) {
            moreText?.text = "... и еще ${hardWords.size - 10} слов"
            moreText?.visibility = View.VISIBLE
        }

        trainButton?.setOnClickListener {
            controller.difficulty = "hard"
            controller.nextWord()
            dialog.dismiss()
        }
    }

    fun showDetailedStats(controller: MainWindow) {
        val context = controller.getAppContext()
        val stats = controller.model.getStats()

        val dialog = AlertDialog.Builder(context)
            .setTitle("📊 Подробная статистика")
            .setView(R.layout.dialog_detailed_stats)
            .setCancelable(true)
            .create()

        dialog.show()

        val statsList = dialog.findViewById<LinearLayout>(R.id.stats_list)
        val closeButton = dialog.findViewById<Button>(R.id.close_button)

        val statsData = listOf(
            Triple("Всего слов", stats["total_words"].toString(), "📚") to Config.COLORS["primary"]!!,
            Triple("Изучено слов", stats["learned_words"].toString(), "🎓") to Config.COLORS["success"]!!,
            Triple("Сложных слов", stats["hard_words"].toString(), "🎯") to Config.COLORS["warning"]!!,
            Triple("Слов сегодня", stats["daily_words"].toString(), "📅") to Config.COLORS["accent"]!!,
            Triple("Правильно сегодня", "${stats["correct_today"]}/${stats["daily_words"]}", "✅") to Config.COLORS["success"]!!,
            Triple("Точность сегодня", String.format("%.1f%%", stats["accuracy_today"]), "🎯") to Config.COLORS["primary"]!!,
            Triple("Процент изучения", String.format("%.1f%%", stats["progress"]), "📈") to Config.COLORS["accent"]!!
        )

        for ((triple, color) in statsData) {
            val (title, value, icon) = triple
            val cardView = createStatsCard(context, title, value, icon, color)
            statsList?.addView(cardView)
        }

        if (stats["total_words"] as Int > 0) {
            val accuracy = (stats["learned_words"] as Int).toDouble() / (stats["total_words"] as Int) * 100
            val extraStats = listOf(
                "Средняя точность: ${String.format("%.1f%%", accuracy)}" to "📊",
                "Слов в работе: ${(stats["total_words"] as Int) - (stats["learned_words"] as Int)}" to "🔄",
                "Текущий язык обучения: ${Config.LANGUAGES[controller.language]?.get("name")}" to "🌐"
            )

            for ((text, icon) in extraStats) {
                val extraView = createExtraStatsCard(context, text, icon)
                statsList?.addView(extraView)
            }
        }

        closeButton?.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun createStatsCard(
        context: Context,
        title: String,
        value: String,
        icon: String,
        color: String
    ): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_stat_card, null)

        view.findViewById<TextView>(R.id.icon_text)?.apply {
            text = icon
            setTextColor(android.graphics.Color.parseColor(color))
        }
        view.findViewById<TextView>(R.id.title_text)?.text = title
        view.findViewById<TextView>(R.id.value_text)?.text = value

        return view
    }

    private fun createExtraStatsCard(context: Context, text: String, icon: String): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_extra_stat, null)
        view.findViewById<TextView>(R.id.stat_text)?.text = "$icon $text"
        return view
    }

    private class HardWordsAdapter(
        private val context: Context,
        private val words: List<Map<String, Any>>
    ) : BaseAdapter() {

        override fun getCount(): Int = words.size

        override fun getItem(position: Int): Map<String, Any> = words[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(
                R.layout.item_hard_word,
                parent,
                false
            )

            val word = words[position]

            val wordText = view.findViewById<TextView>(R.id.word_text)
            val difficultyText = view.findViewById<TextView>(R.id.difficulty_text)

            wordText?.text = "${word["foreign"]} → ${word["translation"]}"
            difficultyText?.text = "${word["difficulty"]}%"

            return view
        }
    }
}

 */