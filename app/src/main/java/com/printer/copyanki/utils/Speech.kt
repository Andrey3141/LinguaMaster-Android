package com.printer.copyanki.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.printer.copyanki.config.Config
import java.util.*

class SpeechSynthesizer(private val context: Context) {

    companion object {
        val EDGE_VOICES = mapOf(
            "ru" to "ru-RU-DariyaNeural",
            "en" to "en-US-JennyNeural",
            "es" to "es-ES-ElviraNeural",
            "fr" to "fr-FR-DeniseNeural",
            "de" to "de-DE-KatjaNeural",
            "zh" to "zh-CN-XiaoxiaoNeural",
            "ja" to "ja-JP-NanamiNeural",
            "ko" to "ko-KR-SunHiNeural",
            "pt" to "pt-BR-FranciscaNeural",
            "it" to "it-IT-ElsaNeural",
            "ar" to "ar-EG-SalmaNeural"
        )

        val EDGE_VOICES_MALE = mapOf(
            "ru" to "ru-RU-MikhailNeural",
            "en" to "en-US-GuyNeural",
            "es" to "es-ES-AlvaroNeural",
            "fr" to "fr-FR-HenriNeural",
            "de" to "de-DE-ConradNeural",
            "zh" to "zh-CN-YunxiNeural",
            "ja" to "ja-JP-KeitaNeural",
            "ko" to "ko-KR-InJoonNeural",
            "pt" to "pt-BR-AntonioNeural",
            "it" to "it-IT-DiegoNeural",
            "ar" to "ar-EG-ShakirNeural"
        )
    }

    var isSpeaking = false
        private set
    var volume = Config.SPEECH_SETTINGS["volume"] as Int
        set(value) {
            field = value.coerceIn(0, 100)
        }
    var speed = (Config.SPEECH_SETTINGS["speed"] as Double).toFloat()
        set(value) {
            field = value.coerceIn(0.5f, 2.0f)
        }
    var enabled = Config.SPEECH_SETTINGS["enabled"] as Boolean
        set(value) {
            field = value
            if (!value) {
                stopCurrent()
            }
        }

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private val mainHandler = Handler(Looper.getMainLooper())
    private var selectedVoices: MutableMap<String, String> = mutableMapOf()

    init {
        loadVoiceSettings()
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsInitialized = true
            }
        }
    }

    // Убираем дублирующие методы setEnabled, setVolume, setSpeed
    // Используем только сеттеры свойств

    private fun loadVoiceSettings() {
        val defaultVoices = mapOf(
            "ru" to "ru-RU",
            "en" to "en-US",
            "es" to "es-ES",
            "fr" to "fr-FR",
            "de" to "de-DE",
            "zh" to "zh-CN",
            "ja" to "ja-JP",
            "ko" to "ko-KR",
            "pt" to "pt-BR",
            "it" to "it-IT",
            "ar" to "ar-EG"
        )

        try {
            val allSettings = SettingsManager.getAll()
            val appSettings = allSettings["app_settings"] as? Map<*, *> ?: emptyMap<Any, Any>()
            val savedVoices = appSettings["selected_voices"] as? Map<*, *> ?: emptyMap<Any, Any>()

            if (savedVoices.isNotEmpty()) {
                for ((lang, voice) in savedVoices) {
                    if (lang is String && voice is String) {
                        selectedVoices[lang] = voice
                    }
                }
            } else {
                for ((lang, voice) in defaultVoices) {
                    selectedVoices[lang] = voice
                }
            }
        } catch (e: Exception) {
            for ((lang, voice) in defaultVoices) {
                selectedVoices[lang] = voice
            }
        }
    }

    private fun saveVoiceSettings(): Boolean {
        return try {
            val allSettings = SettingsManager.getAll().toMutableMap()

            if (!allSettings.containsKey("app_settings")) {
                allSettings["app_settings"] = mutableMapOf<Any, Any>()
            }

            val appSettings = allSettings["app_settings"] as MutableMap<Any, Any>
            appSettings["selected_voices"] = selectedVoices.toMap()

            SettingsManager.saveSettings(allSettings)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isAvailable(): Boolean {
        return isTtsInitialized
    }

    fun getAllVoicesForLanguage(languageCode: String): List<String> {
        val voices = mutableListOf<String>()

        val female = EDGE_VOICES[languageCode]
        val male = EDGE_VOICES_MALE[languageCode]

        if (female != null) {
            voices.add("🌐 $female")
        }
        if (male != null) {
            voices.add("🌐 $male")
        }

        return voices
    }

    fun getDefaultVoiceForLanguage(languageCode: String): String {
        return selectedVoices[languageCode] ?: EDGE_VOICES[languageCode] ?: languageCode
    }

    fun setVoiceForLanguage(languageCode: String, voiceName: String): Boolean {
        val cleanVoice = voiceName
            .replace("🌐 ", "")
            .replace("📱 ", "")

        selectedVoices[languageCode] = cleanVoice
        return saveVoiceSettings()
    }

    fun stopCurrent() {
        if (isTtsInitialized) {
            tts?.stop()
        }
        isSpeaking = false
    }

    fun testVoice(voiceName: String, text: String? = null): Boolean {
        val cleanVoice = voiceName
            .replace("🌐 ", "")
            .replace("📱 ", "")

        var langCode: String? = null
        for ((lang, voice) in EDGE_VOICES) {
            if (voice == cleanVoice) {
                langCode = lang
                break
            }
        }
        if (langCode == null) {
            for ((lang, voice) in EDGE_VOICES_MALE) {
                if (voice == cleanVoice) {
                    langCode = lang
                    break
                }
            }
        }

        if (langCode == null) {
            langCode = "ru"
        }

        val testText = text ?: when (langCode) {
            "ru" -> "Это тестовое предложение"
            "en" -> "This is a test sentence"
            "es" -> "Frase de prueba"
            "fr" -> "Phrase de test"
            "de" -> "Testsatz"
            "zh" -> "测试句子"
            "ja" -> "テスト文"
            "ko" -> "테스트 문장"
            "ar" -> "جملة اختبار"
            else -> "Test sentence"
        }

        speak(testText, langCode, cleanVoice)
        return true
    }

    fun speak(text: String, language: String? = null, forcedVoice: String? = null, callback: ((Boolean) -> Unit)? = null) {
        if (!enabled || text.isEmpty() || !isTtsInitialized) {
            callback?.invoke(false)
            return
        }

        stopCurrent()

        val lang = language ?: "ru"

        isSpeaking = true

        tts?.setSpeechRate(speed)

        val locale = Locale.forLanguageTag(lang)
        tts?.setLanguage(locale)

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}

            override fun onDone(utteranceId: String?) {
                isSpeaking = false
                mainHandler.post {
                    callback?.invoke(true)
                }
            }

            override fun onError(utteranceId: String?) {
                isSpeaking = false
                mainHandler.post {
                    callback?.invoke(false)
                }
            }
        })

        val utteranceId = UUID.randomUUID().toString()
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun speakAsync(text: String, language: String? = null, callback: ((Boolean) -> Unit)? = null) {
        speak(text, language, null, callback)
    }

    fun destroy() {
        tts?.stop()
        tts?.shutdown()
    }
}

lateinit var speechSynth: SpeechSynthesizer