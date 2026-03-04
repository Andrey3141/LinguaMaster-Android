# CopyAnki - Архитектура проекта
## Версия v1.2.0 (2026)

## КОРНЕВАЯ ДИРЕКТОРИЯ (app/src/main/)

├── AndroidManifest.xml
├── java/com/printer/copyanki/
│ ├── MainActivity.kt
│ ├── CHANGELOG.md
│ ├── PROJECT_STRUCTURE.md
│ ├── config/
│ │ └── Config.kt
│ ├── models/
│ │ ├── VocabularyModel.kt
│ │ └── CategoryManager.kt
│ ├── views/
│ │ ├── StatsPanel.kt
│ │ └── (другие view файлы)
│ └── utils/
│ ├── Animations.kt
│ ├── Notifications.kt
│ ├── SettingsManager.kt
│ └── Speech.kt
└── res/
├── layout/
│ ├── activity_main.xml
│ ├── stats_panel.xml
│ └── (другие layout файлы)
├── drawable/
│ ├── toggle_button_attached.xml
│ └── (другие drawable файлы)
└── values/
├── colors.xml
├── strings.xml
├── styles.xml
└── dimens.xml

## ДАННЫЕ (Internal Storage)

/data/user/0/com.printer.copyanki/files/data/
├── vocabulary.json
├── stats.json
└── app_settings.json
