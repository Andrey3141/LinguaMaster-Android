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

object VocabularyDialog {

    fun showVocabulary(controller: MainWindow) {
        val context = controller.getAppContext()

        val dialog = AlertDialog.Builder(context)
            .setTitle("📖 Словарь")
            .setView(R.layout.dialog_vocabulary)
            .setCancelable(false)
            .create()

        dialog.show()

        val showAllCheck = dialog.findViewById<CheckBox>(R.id.show_all_check)
        val categorySpinner = dialog.findViewById<Spinner>(R.id.category_spinner)
        val listView = dialog.findViewById<ListView>(R.id.tree_view)
        val filterInfo = dialog.findViewById<TextView>(R.id.filter_info)
        val closeButton = dialog.findViewById<Button>(R.id.close_button)

        val categories = mutableListOf("Все категории").apply {
            addAll(controller.model.getAllCategories())
        }

        val categoryAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner?.adapter = categoryAdapter

        val wordAdapter = WordListAdapter(context, emptyList())
        listView?.adapter = wordAdapter

        fun loadData() {
            val allWords = controller.model.getAllWords()

            val filteredByLang = if (showAllCheck?.isChecked == true) {
                allWords
            } else {
                allWords.filter { word ->
                    (word["language"] == controller.language && word["native_language"] == controller.nativeLanguage) ||
                            (word["language"] == controller.nativeLanguage && word["native_language"] == controller.language)
                }
            }

            val selectedCategory = categorySpinner?.selectedItem?.toString()
            val currentWords = if (selectedCategory != null && selectedCategory != "Все категории") {
                filteredByLang.filter { word ->
                    (word["category"] as? String ?: "Основные") == selectedCategory
                }
            } else {
                filteredByLang
            }

            wordAdapter.updateData(currentWords)
            filterInfo?.text = "Найдено: ${currentWords.size} слов"
        }

        showAllCheck?.setOnCheckedChangeListener { _, _ -> loadData() }
        categorySpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                loadData()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        loadData()

        closeButton?.setOnClickListener {
            dialog.dismiss()
        }
    }

    private class WordListAdapter(
        private val context: Context,
        private var words: List<Map<String, Any>>
    ) : BaseAdapter() {

        fun updateData(newWords: List<Map<String, Any>>) {
            words = newWords
            notifyDataSetChanged()
        }

        override fun getCount(): Int = words.size

        override fun getItem(position: Int): Map<String, Any> = words[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(
                R.layout.item_word,
                parent,
                false
            )

            val word = words[position]

            val foreignText = view.findViewById<TextView>(R.id.foreign_text)
            val translationText = view.findViewById<TextView>(R.id.translation_text)
            val studyLangText = view.findViewById<TextView>(R.id.study_lang_text)
            val nativeLangText = view.findViewById<TextView>(R.id.native_lang_text)
            val categoryText = view.findViewById<TextView>(R.id.category_text)
            val difficultyText = view.findViewById<TextView>(R.id.difficulty_text)

            foreignText?.text = word["foreign"] as String
            translationText?.text = word["translation"] as String

            val studyLang = word["language"] as String
            val nativeLang = word["native_language"] as String

            studyLangText?.text = Config.LANGUAGES[studyLang]?.get("name") as String
            nativeLangText?.text = Config.LANGUAGES[nativeLang]?.get("name") as String
            categoryText?.text = word["category"] as? String ?: "Основные"

            val difficulty = word["difficulty"] as Int
            difficultyText?.text = "$difficulty%"

            when {
                difficulty <= 50 -> difficultyText?.setTextColor(
                    android.graphics.Color.parseColor(Config.COLORS["danger"])
                )
                difficulty >= 80 -> difficultyText?.setTextColor(
                    android.graphics.Color.parseColor(Config.COLORS["success"])
                )
                else -> difficultyText?.setTextColor(
                    android.graphics.Color.parseColor(Config.COLORS["warning"])
                )
            }

            return view
        }
    }
}

 */