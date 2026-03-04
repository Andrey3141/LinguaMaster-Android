package com.printer.copyanki.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.printer.copyanki.config.Config
import org.json.JSONObject
import java.io.File

object SettingsManager {

    private const val PREFS_NAME = "app_settings"
    private const val SETTINGS_FILE = "app_settings.json"

    private lateinit var context: Context
    private var settings = mutableMapOf<String, Any>()

    fun init(context: Context) {
        this.context = context.applicationContext
        loadSettings()
    }

    private fun getDataPath(filename: String): String {
        val dataDir = File(context.filesDir, Config.PATHS["data"] as String)
        if (!dataDir.exists()) {
            dataDir.mkdirs()
        }
        return File(dataDir, filename).absolutePath
    }

    fun loadSettings() {
        val filePath = getDataPath(SETTINGS_FILE)
        val file = File(filePath)

        if (file.exists()) {
            try {
                val content = file.readText()
                val jsonObject = JSONObject(content)
                settings.clear()

                val keys = jsonObject.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val value = jsonObject.get(key)
                    settings[key] = when (value) {
                        is JSONObject -> convertJsonObjectToMap(value)
                        else -> value
                    }
                }

                println("✅ Загружены настройки из $filePath")
            } catch (e: Exception) {
                println("Ошибка загрузки настроек: ${e.message}")
                settings.clear()
            }
        } else {
            settings.clear()
        }
    }

    private fun convertJsonObjectToMap(jsonObject: JSONObject): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        val keys = jsonObject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = jsonObject.get(key)
            map[key] = when (value) {
                is JSONObject -> convertJsonObjectToMap(value)
                else -> value
            }
        }
        return map
    }

    fun saveSettings(settings: Map<String, Any>): Boolean {
        return try {
            val filePath = getDataPath(SETTINGS_FILE)
            val file = File(filePath)

            val jsonObject = JSONObject()
            for ((key, value) in settings) {
                when (value) {
                    is Map<*, *> -> jsonObject.put(key, convertMapToJsonObject(value as Map<String, Any>))
                    else -> jsonObject.put(key, value)
                }
            }

            file.parentFile?.mkdirs()
            file.writeText(jsonObject.toString(2))

            this.settings.clear()
            this.settings.putAll(settings)

            println("✅ Сохранены настройки в $filePath")
            true
        } catch (e: Exception) {
            println("Ошибка сохранения настроек: ${e.message}")
            false
        }
    }

    private fun convertMapToJsonObject(map: Map<String, Any>): JSONObject {
        val jsonObject = JSONObject()
        for ((key, value) in map) {
            when (value) {
                is Map<*, *> -> jsonObject.put(key, convertMapToJsonObject(value as Map<String, Any>))
                else -> jsonObject.put(key, value)
            }
        }
        return jsonObject
    }

    fun get(key: String, default: Any? = null): Any? {
        return settings[key] ?: default
    }

    fun getAll(): Map<String, Any> {
        return settings.toMap()
    }
}