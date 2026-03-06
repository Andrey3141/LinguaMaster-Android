package com.printer.copyanki.config

object Config {

    // Цветовая схема (темная тема)
    val COLORS = mapOf(
        "bg_dark" to "#0f172a",
        "bg_card" to "#1e293b",
        "primary" to "#3b82f6",
        "secondary" to "#10b981",
        "accent" to "#8b5cf6",
        "text" to "#f1f5f9",
        "text_secondary" to "#94a3b8",
        "danger" to "#ef4444",
        "warning" to "#f59e0b",
        "success" to "#10b981",
        "border" to "#334155",
        "speaker" to "#8b5cf6",
        "speaker_hover" to "#7c3aed",
        "speaker_disabled" to "#4a4a4a"
    )

    // Размеры окон
    val WINDOW_SIZES = mapOf(
        "main" to "1920x1080",
        "add_word" to "500x350",
        "view_vocabulary" to "900x600",
        "hard_words" to "500x400",
        "stats" to "600x600",
        "settings" to "700x550",
        "language" to "700x500",
        "voice_settings" to "500x600"
    )

    // Шрифты
    val FONTS = mapOf(
        "title" to listOf("Segoe UI", 20, "bold"),
        "subtitle" to listOf("Segoe UI", 10),
        "heading" to listOf("Segoe UI", 13, "bold"),
        "body" to listOf("Segoe UI", 10),
        "body_small" to listOf("Segoe UI", 9),
        "word_display" to listOf("Segoe UI", 36, "bold"),
        "input" to listOf("Segoe UI", 16),
        "speaker_icon" to listOf("Segoe UI", 14)
    )

    // Поддерживаемые языки
    val LANGUAGES = mapOf(
        "en" to mapOf("name" to "Английский", "flag" to "🇺🇸", "code" to "en"),
        "ru" to mapOf("name" to "Русский", "flag" to "🇷🇺", "code" to "ru"),
        "es" to mapOf("name" to "Испанский", "flag" to "🇪🇸", "code" to "es"),
        "fr" to mapOf("name" to "Французский", "flag" to "🇫🇷", "code" to "fr"),
        "de" to mapOf("name" to "Немецкий", "flag" to "🇩🇪", "code" to "de"),
        "zh" to mapOf("name" to "Китайский", "flag" to "🇨🇳", "code" to "zh"),
        "ja" to mapOf("name" to "Японский", "flag" to "🇯🇵", "code" to "ja"),
        "ko" to mapOf("name" to "Корейский", "flag" to "🇰🇷", "code" to "ko"),
        "pt" to mapOf("name" to "Португальский", "flag" to "🇵🇹", "code" to "pt"),
        "it" to mapOf("name" to "Итальянский", "flag" to "🇮🇹", "code" to "it"),
        "ar" to mapOf("name" to "Арабский", "flag" to "🇸🇦", "code" to "ar")
    )

    // Настройки тренировки
    val TRAINING_SETTINGS = mapOf(
        "default_language" to "en",
        "native_language" to "ru",
        "quick_training_words" to 10,
        "hard_word_threshold" to 50,
        "easy_word_threshold" to 80,
        "auto_advance" to true,
        "show_hints" to true,
        "prevent_repeats" to true
    )

    // Настройки озвучки
    val SPEECH_SETTINGS = mapOf(
        "enabled" to true,
        "auto_speak" to false,
        "volume" to 100,
        "speed" to 1.0,
        "selected_voices" to mapOf(
            "ru" to "anna",
            "en" to "slt",
            "es" to "elena",
            "fr" to "celia",
            "de" to "yuriy",
            "zh" to "mikhail",
            "ja" to "mikhail",
            "ko" to "mikhail",
            "pt" to "maria",
            "it" to "aleksandr",
            "ar" to "anna"
        )
    )

    // Файлы
    val FILES = mapOf(
        "vocabulary" to "vocabulary.json",
        "user_settings" to "settings.json"
    )

    // Пути к ресурсам
    val PATHS = mapOf(
        "data" to "data/",
        "backups" to "backups/"
    )

    // Тексты
    val TEXTS = mapOf(
        "app_name" to "LinguaMaster",
        "app_subtitle" to "Тренажер иностранных слов",
        "version" to "v1.2.1",
        "year" to "2026"
    )
}