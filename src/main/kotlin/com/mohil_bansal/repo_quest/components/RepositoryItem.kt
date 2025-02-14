package com.mohil_bansal.repo_quest.components;

class RepositoryItem{
    var title: String? = null
    var path: String? = null

    fun getValueByColumn(columnName: String): Any {
        return when (columnName) {
            "Title" -> title ?: ""
            "Path" -> path ?: ""
            else -> ""
        }
    }
}