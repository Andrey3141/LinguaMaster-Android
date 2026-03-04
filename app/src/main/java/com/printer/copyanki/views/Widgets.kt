package com.printer.copyanki.views

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.printer.copyanki.R
import com.printer.copyanki.config.Config

// ModernButton - аналог ModernButton из widgets.py
class ModernButton(
    context: Context,
    private val hoverColor: Int = Color.parseColor(Config.COLORS["primary"]),
    private val originalColor: Int = Color.parseColor(Config.COLORS["bg_card"])
) : androidx.appcompat.widget.AppCompatButton(context) {

    init {
        setBackgroundColor(originalColor)
        setTextColor(Color.parseColor(Config.COLORS["text"]))
        setPadding(30, 20, 30, 20)

        setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    setBackgroundColor(hoverColor)
                    v.invalidate()
                }
                android.view.MotionEvent.ACTION_UP,
                android.view.MotionEvent.ACTION_CANCEL -> {
                    setBackgroundColor(originalColor)
                    v.invalidate()
                    performClick()
                }
            }
            true
        }
    }
}

// CardFrame - аналог CardFrame из widgets.py
class CardFrame(context: Context) : FrameLayout(context) {

    init {
        setBackgroundColor(Color.parseColor(Config.COLORS["bg_card"]))
        setPadding(30, 30, 30, 30)
    }
}

// IconLabel - аналог IconLabel из widgets.py
class IconLabel(context: Context, icon: String, text: String) : TextView(context) {

    init {
        this.text = "$icon  $text"
        setTextColor(Color.parseColor(Config.COLORS["text"]))
        textSize = 14f
    }
}

// ProgressCard - аналог ProgressCard из widgets.py
class ProgressCard(
    context: Context,
    private val title: String,
    private var value: Int = 0,
    private var maxValue: Int = 100
) : FrameLayout(context) {

    private var valueTextView: TextView
    private var progressFill: View

    init {
        setBackgroundColor(Color.parseColor(Config.COLORS["bg_card"]))

        // Заголовок
        val titleView = TextView(context).apply {
            text = title
            setTextColor(Color.parseColor(Config.COLORS["text_secondary"]))
            textSize = 12f
        }
        addView(titleView)

        // Фрейм для прогресс-бара
        val progressFrame = FrameLayout(context).apply {
            setBackgroundColor(Color.parseColor(Config.COLORS["bg_dark"]))
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 30)
        }
        addView(progressFrame)

        // Заполнение прогресс-бара
        progressFill = View(context).apply {
            setBackgroundColor(Color.parseColor(Config.COLORS["primary"]))
            layoutParams = LayoutParams(0, 30)
        }
        progressFrame.addView(progressFill)

        // Значение
        valueTextView = TextView(context).apply {
            text = "$value/$maxValue"
            setTextColor(Color.parseColor(Config.COLORS["text"]))
            textSize = 14f
        }
        addView(valueTextView)
    }

    fun setValue(newValue: Int, newMaxValue: Int = maxValue) {
        value = newValue
        maxValue = newMaxValue
        valueTextView.text = "$value/$maxValue"

        val percentage = value.toFloat() / maxValue.toFloat()
        progressFill.layoutParams.width = ((progressFill.parent as View).width * percentage).toInt()
        progressFill.requestLayout()
    }
}

// StatsCard - аналог StatsCard из widgets.py
class StatsCard(
    context: Context,
    private val title: String,
    private var value: String,
    private val icon: String,
    private val color: String
) : FrameLayout(context) {

    private var valueTextView: TextView

    init {
        setBackgroundColor(Color.parseColor(Config.COLORS["bg_card"]))

        // Иконка
        val iconView = TextView(context).apply {
            text = icon
            textSize = 24f
            setTextColor(Color.parseColor(color))
        }
        addView(iconView)

        // Текст
        val textFrame = FrameLayout(context)

        val titleView = TextView(context).apply {
            text = title
            setTextColor(Color.parseColor(Config.COLORS["text_secondary"]))
            textSize = 12f
        }
        textFrame.addView(titleView)

        valueTextView = TextView(context).apply {
            text = value
            setTextColor(Color.parseColor(Config.COLORS["text"]))
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        textFrame.addView(valueTextView)

        addView(textFrame)
    }

    fun updateValue(newValue: String) {
        value = newValue
        valueTextView.text = value
    }
}