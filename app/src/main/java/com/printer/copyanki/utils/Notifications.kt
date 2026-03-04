package com.printer.copyanki.utils

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.printer.copyanki.R
import com.printer.copyanki.config.Config
import java.util.*

object Notifications {

    private val mainHandler = Handler(Looper.getMainLooper())

    /**
     * Показывает всплывающее уведомление
     * @param context Контекст
     * @param title Заголовок уведомления
     * @param message Текст сообщения
     * @param type Тип уведомления ('info', 'success', 'warning', 'error')
     * @param duration Длительность показа в миллисекундах
     */
    fun showNotification(
        context: Context,
        title: String,
        message: String,
        type: String = "info",
        duration: Long = 2000
    ) {
        // Цвета для разных типов уведомлений
        val colors = mapOf(
            "info" to Config.COLORS["primary"],
            "success" to Config.COLORS["success"],
            "warning" to Config.COLORS["warning"],
            "error" to Config.COLORS["danger"]
        )

        val bgColor = colors[type] ?: Config.COLORS["primary"]

        // Иконка для типа уведомления
        val icons = mapOf(
            "info" to "ℹ️",
            "success" to "✅",
            "warning" to "⚠️",
            "error" to "❌"
        )

        val icon = icons[type] ?: "ℹ️"

        // Создаем кастомный Toast
        val toast = Toast(context)
        val layout = LayoutInflater.from(context).inflate(R.layout.custom_toast, null)

        layout.setBackgroundColor(android.graphics.Color.parseColor(bgColor))

        val iconView = layout.findViewById<TextView>(R.id.toast_icon)
        iconView.text = icon
        iconView.setTextColor(android.graphics.Color.parseColor(Config.COLORS["text"]))

        val titleView = layout.findViewById<TextView>(R.id.toast_title)
        titleView.text = title
        titleView.setTextColor(android.graphics.Color.parseColor(Config.COLORS["text"]))

        val messageView = layout.findViewById<TextView>(R.id.toast_message)
        messageView.text = message
        messageView.setTextColor(android.graphics.Color.parseColor(Config.COLORS["text"]))

        toast.view = layout
        toast.duration = if (duration > 3000) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    /**
     * Показывает тост-уведомление
     * @param context Контекст
     * @param message Сообщение
     * @param position Позиция ('top', 'bottom', 'center')
     * @param duration Длительность показа
     */
    fun showToast(
        context: Context,
        message: String,
        position: String = "bottom",
        duration: Long = 1500
    ) {
        val toast = Toast(context)
        val layout = LayoutInflater.from(context).inflate(R.layout.simple_toast, null)

        layout.setBackgroundColor(android.graphics.Color.parseColor(Config.COLORS["bg_card"]))
        layout.alpha = 0.9f

        val messageView = layout.findViewById<TextView>(R.id.toast_message)
        messageView.text = message
        messageView.setTextColor(android.graphics.Color.parseColor(Config.COLORS["text"]))

        toast.view = layout
        toast.duration = if (duration > 3000) Toast.LENGTH_LONG else Toast.LENGTH_SHORT

        when (position) {
            "top" -> toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 100)
            "center" -> toast.setGravity(Gravity.CENTER, 0, 0)
            else -> toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 100)
        }

        toast.show()
    }

    /**
     * Показывает диалог подтверждения
     * @param context Контекст
     * @param title Заголовок диалога
     * @param message Сообщение
     * @param onConfirm Функция при подтверждении
     * @param onCancel Функция при отмене (опционально)
     */
    fun showConfirmationDialog(
        context: Context,
        title: String,
        message: String,
        onConfirm: () -> Unit,
        onCancel: (() -> Unit)? = null
    ) {
        val dialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Подтвердить") { _, _ ->
                onConfirm()
            }
            .setNegativeButton("Отмена") { _, _ ->
                onCancel?.invoke()
            }
            .create()

        dialog.show()

        // Настройка цветов (требуется API 21+)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
            android.graphics.Color.parseColor(Config.COLORS["success"])
        )
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
            android.graphics.Color.parseColor(Config.COLORS["text_secondary"])
        )
    }
}