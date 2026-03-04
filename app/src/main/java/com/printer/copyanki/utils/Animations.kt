package com.printer.copyanki.utils

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.*
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import com.printer.copyanki.config.Config

object Animations {

    private val mainHandler = Handler(Looper.getMainLooper())

    /**
     * Анимация успешного ответа
     * @param view Виджет для анимации
     * @param callback Функция обратного вызова после анимации
     */
    fun animateSuccess(view: View, callback: (() -> Unit)? = null) {
        val originalColor = view.solidColor
        val successColor = android.graphics.Color.parseColor(Config.COLORS["success"])

        // Первая фаза: зеленый фон
        val colorAnim = ValueAnimator.ofObject(ArgbEvaluator(), originalColor, successColor)
        colorAnim.duration = 150
        colorAnim.addUpdateListener { animator ->
            view.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnim.doOnEnd {
            // Вторая фаза: возврат к исходному цвету
            val returnAnim = ValueAnimator.ofObject(ArgbEvaluator(), successColor, originalColor)
            returnAnim.duration = 150
            returnAnim.addUpdateListener { animator ->
                view.setBackgroundColor(animator.animatedValue as Int)
            }
            returnAnim.doOnEnd {
                callback?.invoke()
            }
            returnAnim.start()
        }
        colorAnim.start()
    }

    /**
     * Анимация ошибки
     * @param view Виджет для анимации
     * @param callback Функция обратного вызова после анимации
     */
    fun animateError(view: View, callback: (() -> Unit)? = null) {
        val originalColor = view.solidColor
        val errorColor = android.graphics.Color.parseColor(Config.COLORS["danger"])

        // Первая фаза: красный фон
        val colorAnim = ValueAnimator.ofObject(ArgbEvaluator(), originalColor, errorColor)
        colorAnim.duration = 150
        colorAnim.addUpdateListener { animator ->
            view.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnim.doOnEnd {
            // Вторая фаза: возврат к исходному цвету
            val returnAnim = ValueAnimator.ofObject(ArgbEvaluator(), errorColor, originalColor)
            returnAnim.duration = 150
            returnAnim.addUpdateListener { animator ->
                view.setBackgroundColor(animator.animatedValue as Int)
            }
            returnAnim.doOnEnd {
                callback?.invoke()
            }
            returnAnim.start()
        }
        colorAnim.start()
    }

    /**
     * Плавное появление виджета
     * @param view Виджет для анимации
     * @param duration Длительность анимации в мс
     * @param callback Функция обратного вызова после анимации
     */
    fun fadeIn(view: View, duration: Long = 500, callback: (() -> Unit)? = null) {
        view.alpha = 0f
        view.visibility = View.VISIBLE

        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                callback?.invoke()
            }
            .start()
    }

    /**
     * Анимация тряски виджета (для ошибок)
     * @param view Виджет для анимации
     * @param intensity Интенсивность тряски
     * @param duration Длительность анимации в мс
     * @param callback Функция обратного вызова после анимации
     */
    fun shakeView(view: View, intensity: Float = 5f, duration: Long = 300, callback: (() -> Unit)? = null) {
        val shake = AnimationUtils.loadAnimation(view.context, android.R.anim.slide_in_left)
        shake.interpolator = CycleInterpolator(5f)
        shake.duration = duration

        view.startAnimation(shake)

        mainHandler.postDelayed({
            callback?.invoke()
        }, duration)
    }

    /**
     * Пульсирующая анимация
     * @param view Виджет для анимации
     * @param color Цвет пульсации
     * @param duration Длительность анимации в мс
     * @param callback Функция обратного вызова после анимации
     */
    fun pulseAnimation(view: View, color: String, duration: Long = 1000, callback: (() -> Unit)? = null) {
        val originalColor = view.solidColor
        val pulseColor = android.graphics.Color.parseColor(color)

        val animator = ValueAnimator.ofObject(ArgbEvaluator(), originalColor, pulseColor, originalColor)
        animator.duration = duration
        animator.repeatCount = 1
        animator.addUpdateListener { anim ->
            view.setBackgroundColor(anim.animatedValue as Int)
        }
        animator.doOnEnd {
            callback?.invoke()
        }
        animator.start()
    }

    /**
     * Эффект печатной машинки
     * @param textView TextView для текста
     * @param text Текст для отображения
     * @param delay Задержка между символами в мс
     * @param callback Функция обратного вызова после анимации
     */
    fun typewriterEffect(textView: TextView, text: String, delay: Long = 50, callback: (() -> Unit)? = null) {
        textView.text = ""
        val currentText = StringBuilder()

        fun typeChar(index: Int) {
            if (index < text.length) {
                currentText.append(text[index])
                textView.text = currentText.toString()
                mainHandler.postDelayed({
                    typeChar(index + 1)
                }, delay)
            } else {
                callback?.invoke()
            }
        }

        typeChar(0)
    }

    /**
     * Анимация счетчика с увеличением значения
     * @param textView TextView для отображения значения
     * @param startValue Начальное значение
     * @param endValue Конечное значение
     * @param duration Длительность анимации в мс
     * @param format Формат отображения (по умолчанию "%.0f")
     * @param callback Функция обратного вызова после анимации
     */
    fun countUpAnimation(
        textView: TextView,
        startValue: Float,
        endValue: Float,
        duration: Long = 1000,
        format: String = "%.0f",
        callback: (() -> Unit)? = null
    ) {
        val animator = ValueAnimator.ofFloat(startValue, endValue)
        animator.duration = duration
        animator.interpolator = AccelerateDecelerateInterpolator()

        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            textView.text = String.format(format, value)
        }

        animator.doOnEnd {
            textView.text = String.format(format, endValue)
            callback?.invoke()
        }

        animator.start()
    }
}