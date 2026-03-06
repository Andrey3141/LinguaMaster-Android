**Тренажер иностранных слов для Android** — современное приложение для изучения лексики с использованием интервальных повторений, написанное на Kotlin.

[![Platform](https://img.shields.io/badge/platform-Android-green.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-purple.svg)](https://kotlinlang.org/)
[![API](https://img.shields.io/badge/API-24+-blue.svg)](https://android-arsenal.com/api?level=24)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

---

## 📱 Описание

CopyAnki — это мощный и удобный тренажер для изучения иностранных слов, адаптированный для мобильных устройств. Приложение помогает эффективно запоминать новую лексику благодаря:

- ✅ Системе интервальных повторений
- ✅ Трем режимам обучения (ручной, тест, сопоставление)
- ✅ Встроенному синтезу речи (Android TTS)
- ✅ Детальной статистике прогресса
- ✅ Темному адаптивному интерфейсу

---

## ✨ Особенности

### 🎯 Три режима обучения

| Режим | Описание | Файл |
|-------|----------|------|
| ✍️ **Ручной перевод** | Вводите перевод слова вручную с умными подсказками | `TrainingPanel.kt` |
| 📝 **Тест** | Выбирайте правильный вариант из 2-3 предложенных | `TestPanel.kt` |
| 🔄 **Соотношение** | Сопоставляйте слова с переводами в интерактивном режиме | `MatchPanel.kt` |

### 🌍 Поддерживаемые языки (11 языковых пар)

```kotlin
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
```

### 🔊 Озвучка (Android TTS)

- Автоматическое определение языка слова
- Кнопка динамика рядом с каждым словом
- Автоматическая озвучка новых слов (опционально)
- Настройка громкости (0–100%) и скорости речи (0.5×–2.0×)
- Выбор голосов для каждого языка через диалог настроек

### 📊 Статистика обучения

Панель статистики (выезжает слева) отображает:

```
📚 всего слов      0
🎓 изучено         0
🎯 сложные         0
📅 сегодня         0

📈 общий прогресс
[████████░░] 45%

📈 прогресс дня
[█████████░] 87%
```

**Дополнительно в диалоге детальной статистики:**
- История обучения по дням
- Общее время в приложении
- Средняя точность за все время
- Список сложных слов с возможностью быстрой тренировки

### 🎨 Интерфейс

- Современный темный дизайн (цвета из `colors.xml`)
- Плавные анимации успеха/ошибки (`Animations.kt`)
- Выезжающие панели статистики и управления
- Адаптивная верстка под разные размеры экранов
- Полноэкранный режим (скрыта системная панель)

### ⚙️ Гибкие настройки

```kotlin
val TRAINING_SETTINGS = mapOf(
    "default_language" to "en",
    "native_language" to "ru",
    "quick_training_words" to 10,
    "hard_word_threshold" to 50,      // Порог сложных слов (%)
    "easy_word_threshold" to 80,      // Порог изученных слов (%)
    "auto_advance" to true,           // Автопереход после правильного ответа
    "show_hints" to true,             // Показывать подсказки
    "prevent_repeats" to true         // Не повторять слова в сессии
)
```

---

## 📥 Установка

### Требования

| Компонент | Версия |
|-----------|--------|
| Android OS | 7.0+ (API 24) |
| Свободное место | 10 MB |
| Интернет | Для загрузки голосов TTS (опционально) |

### Сборка из исходников

```bash
# 1. Клонируйте репозиторий
git clone https://github.com/yourusername/CopyAnki.git
cd CopyAnki

# 2. Откройте в Android Studio (Arctic Fox+)
#    File → Open → выберите папку проекта

# 3. Соберите debug-версию
./gradlew assembleDebug

# 4. Установите на устройство
adb install app/build/outputs/apk/debug/app-debug.apk
```

Или скачайте готовый APK из [Releases](https://github.com/yourusername/CopyAnki/releases).

---

## 🚀 Быстрый старт

1. **Запустите приложение** — откроется главный экран с полем для слова
2. **Добавьте слова** — нажмите `➕ Добавить слово` в панели управления
3. **Выберите режим** — `🎓 Метод` → ручной / тест / соотношение
4. **Начните тренировку** — вводите переводы или выбирайте варианты
5. **Следите за прогрессом** — панель статистики выезжает слева

---

## 📖 Использование

### Добавление слов

1. Нажмите **"➕ Добавить слово"** (панель управления справа)
2. Введите:
   - Иностранное слово
   - Перевод на родной язык
   - Категорию (или создайте новую)
3. Нажмите **"✅ Добавить"**

Слово автоматически сохранится в `vocabulary.json`.

### Режимы обучения

#### ✍️ Ручной перевод
```
[слово] 🔊
Введите перевод: [__________]
[✅ Проверить]
```
- Вводите перевод вручную
- Подсказка появляется после заданного числа ошибок (настраивается)
- Автоматическая проверка и анимация результата

#### 📝 Тест
```
[слово] 🔊
Выберите правильный перевод:
[ ] вариант 1
[✓] вариант 2  ← правильный
[ ] вариант 3
```
- 2–3 варианта ответа
- Визуальная подсветка правильного/неправильного выбора
- Идеально для быстрой проверки знаний

#### 🔄 Соотношение
```
Английский:          Русский:
[ hello ]           [ книга ]
[ world ]     ↔     [ привет ]  ← правильно!
[ book  ]           [ мир     ]
```
- Перетаскивание или нажатие для сопоставления пар
- Интерактивный режим для визуалов
- Минимум 3 слова для запуска режима

### Настройка озвучки

1. Откройте **"⚙️ Настройки"**
2. Перейдите в **"🎤 Выбрать голоса для языков"**
3. Для каждого языка:
   - Выберите голос из списка (🌐 женские / 🎤 мужские)
   - Нажмите **"▶"** для теста
4. Сохраните настройки

**Доступные голоса (Edge Neural):**
```kotlin
val EDGE_VOICES = mapOf(
    "ru" to "ru-RU-DariyaNeural", "en" to "en-US-JennyNeural",
    "es" to "es-ES-ElviraNeural", "fr" to "fr-FR-DeniseNeural",
    "de" to "de-DE-KatjaNeural",  "zh" to "zh-CN-XiaoxiaoNeural",
    "ja" to "ja-JP-NanamiNeural", "ko" to "ko-KR-SunHiNeural",
    "pt" to "pt-BR-FranciscaNeural", "it" to "it-IT-ElsaNeural",
    "ar" to "ar-EG-SalmaNeural"
)
```

### Статистика

**Панель слева (выезжает):**
| Элемент | Описание |
|---------|----------|
| 📚 всего слов | Общее количество слов в словаре |
| 🎓 изучено | Слова с `difficulty ≥ 80%` |
| 🎯 сложные | Слова с `difficulty ≤ 50%` |
| 📅 сегодня | Слов пройдено за текущую сессию |
| 📈 общий прогресс | `% изученных слов` с визуальным баром |
| 📈 прогресс дня | `% точности сегодня` с визуальным баром |

**Диалог детальной статистики:**
- История по дням (дата, слова, точность, время)
- Общее время обучения
- Средняя точность за все время
- Кнопка быстрой тренировки сложных слов

---

## 🏗️ Архитектура проекта

```
app/src/main/
├── AndroidManifest.xml
├── java/com/printer/copyanki/
│   ├── MainActivity.kt              # Точка входа, основной контроллер
│   ├── config/
│   │   └── Config.kt                # Все настройки: цвета, языки, пути
│   ├── models/
│   │   ├── VocabularyModel.kt       # Словарь, статистика, проверка ответов
│   │   └── CategoryManager.kt       # Альтернативный менеджер словаря
│   ├── views/
│   │   ├── StatsPanel.kt            # Левая панель статистики
│   │   ├── ControlPanel.kt          # Правая панель управления
│   │   ├── BottomBar.kt             # Нижняя панель (версия, статус)
│   │   ├── Widgets.kt               # Кастомные виджеты (ModernButton и др.)
│   │   ├── TrainingPanel.kt         # Режим ручного перевода
│   │   ├── TestPanel.kt             # Режим теста
│   │   ├── MatchPanel.kt            # Режим сопоставления
│   │   ├── TopBar.kt                # Верхняя панель (режимы, статистика)
│   │   └── DialogHandlers.kt        # Обработчики диалогов
│   └── utils/
│       ├── Speech.kt                # SpeechSynthesizer (Android TTS)
│       ├── Animations.kt            # Анимации (успех, ошибка, счётчики)
│       ├── Notifications.kt         # Toast, диалоги подтверждения
│       └── SettingsManager.kt       # Сохранение/загрузка настроек (JSON)
└── res/
    ├── layout/                      # XML разметка
    │   ├── activity_main.xml        # Главный экран
    │   ├── stats_panel.xml          # Панель статистики
    │   ├── control_panel.xml        # Панель управления
    │   ├── bottom_bar.xml           # Нижняя панель
    │   ├── dialog_*.xml             # Диалоги (7 файлов)
    │   └── item_*.xml               # Элементы списков
    ├── drawable/                    # Графические ресурсы
    │   ├── toggle_button_attached.xml
    │   ├── toggle_button_control.xml
    │   └── toast_background.xml
    └── values/
        ├── colors.xml               # Цветовая схема (темная тема)
        ├── strings.xml              # Текстовые ресурсы
        ├── styles.xml               # Стили и темы
        └── dimens.xml               # Размеры
```

### Хранение данных (Internal Storage)

```
/data/user/0/com.printer.copyanki/files/data/
├── vocabulary.json      # Словарь пользователя
├── stats.json           # Статистика обучения (история по дням)
└── app_settings.json    # Настройки приложения
```

**Формат vocabulary.json:**
```json
[
  {
    "foreign": "hello",
    "translation": "привет",
    "language": "en",
    "native_language": "ru",
    "category": "Основные",
    "added_date": "2026-02-24",
    "last_review": "2026-02-26 14:30:00",
    "review_count": 5,
    "correct_count": 4,
    "incorrect_count": 1,
    "difficulty": 75
  }
]
```

---

## 🛠️ Технологии

| Компонент | Версия / Описание |
|-----------|------------------|
| **Язык** | Kotlin 1.9+ |
| **Min SDK** | 24 (Android 7.0) |
| **Target SDK** | 34 (Android 14) |
| **UI** | Android Views + XML Layouts |
| **Анимации** | ObjectAnimator, ValueAnimator |
| **Озвучка** | Android Text-to-Speech (TTS) |
| **Хранение** | JSON файлы в internal storage |
| **Сборка** | Gradle Kotlin DSL |

### Зависимости (`build.gradle.kts`)

```kotlin
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
}
```

---

## ⚙️ Конфигурация (Config.kt)

Все настройки централизованы в `Config.kt`:

```kotlin
object Config {
    // Цветовая схема (темная тема)
    val COLORS = mapOf(
        "bg_dark" to "#0f172a", "bg_card" to "#1e293b",
        "primary" to "#3b82f6", "secondary" to "#10b981",
        "accent" to "#8b5cf6", "text" to "#f1f5f9",
        "text_secondary" to "#94a3b8", "danger" to "#ef4444",
        "warning" to "#f59e0b", "success" to "#10b981",
        "border" to "#334155", "speaker" to "#8b5cf6",
        "speaker_hover" to "#7c3aed", "speaker_disabled" to "#4a4a4a"
    )

    // Поддерживаемые языки (11 языков)
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
            "ru" to "anna", "en" to "slt", "es" to "elena",
            "fr" to "celia", "de" to "yuriy", "zh" to "mikhail",
            "ja" to "mikhail", "ko" to "mikhail", "pt" to "maria",
            "it" to "aleksandr", "ar" to "anna"
        )
    )

    // Тексты (версия, название, год)
    val TEXTS = mapOf(
        "app_name" to "LinguaMaster",
        "app_subtitle" to "Тренажер иностранных слов",
        "version" to "v1.2.1",
        "year" to "2026"
    )
}
```

---

## 🔧 Разработка

### Внесение изменений

```bash
# 1. Создайте ветку для фичи
git checkout -b feature/amazing-feature

# 2. Внесите изменения и протестируйте
#    - Запустите эмулятор или подключите устройство
#    - Соберите: ./gradlew assembleDebug
#    - Установите: adb install app-debug.apk

# 3. Закоммитьте изменения
git commit -m "feat: add amazing feature"

# 4. Отправьте в репозиторий
git push origin feature/amazing-feature

# 5. Откройте Pull Request
```

### Сборка релиза

```bash
# 1. Подпишите ключом (создайте keystore, если нет)
./gradlew assembleRelease

# 2. APK будет в:
app/build/outputs/apk/release/app-release.apk

# 3. Протестируйте на реальном устройстве перед публикацией
```

### Отладка

```bash
# Логи приложения
adb logcat | grep MainActivity

# Просмотр файлов данных
adb shell "ls /data/user/0/com.printer.copyanki/files/data/"

# Копирование базы для анализа
adb pull /data/user/0/com.printer.copyanki/files/data/vocabulary.json
```

---

## 🤝 Вклад в проект

Приветствуются любые contributions! 🙏

1. **Fork** репозиторий
2. Создайте ветку: `git checkout -b feature/YourFeature`
3. Внесите изменения и **протестируйте**
4. Закоммитьте: `git commit -m 'feat: add YourFeature'`
5. Отправьте: `git push origin feature/YourFeature`
6. Откройте **Pull Request** с описанием изменений

---

## 📄 Лицензия

Этот проект распространяется под лицензией **MIT**.  
См. файл [LICENSE](LICENSE) для подробностей.

---

## 👥 Авторы и благодарности

### Авторы
- [Мой профиль](https://github.com/Andrey3141)

### Благодарности
- 🙏 [Android Developers](https://developer.android.com/) — документация, гайды и лучшие практики
- 🙏 [Kotlin](https://kotlinlang.org/) — прекрасный язык для современной разработки под Android
- 🙏 [Edge TTS / RHVoice](https://github.com/rhvoice/rhvoice) — идеи для системы озвучки

---

## 📞 Контакты и поддержка

| Канал | Ссылка |
|-------|--------|
| 🐛 Баги и фичи | [GitHub Issues](https://github.com/yourusername/CopyAnki/issues) |
| 💬 Обсуждения | [GitHub Discussions](https://github.com/yourusername/CopyAnki/discussions) |
| 📧 Email | askackov08@gmail.com |
| ✈️ Telegram | [@tools271](https://t.me/tools271) |

---

<div align="center">

### 🎓 CopyAnki

**Учите слова. Эффективно. С удовольствием.**

[⭐ Поставьте звезду](https://github.com/Andrey3141/LinguaMaster-Android), если проект вам полезен!

```
Made with ❤️ using Kotlin & Android SDK
```

</div>
