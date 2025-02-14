package com.mohil_bansal.repo_quest.components


class PackageItem {
    var packageName: String? = null
    var description: String? = null
    var author: String? = null
    var lastVersion: String? = null

    fun getValueByColumn(columnName: String): Any {
        return when (columnName) {
            "Package Name" -> packageName ?: ""
            "Author" -> author ?: ""
            "Last Version" -> lastVersion ?: ""
            "Description" -> description ?: ""
            else -> ""
        }
    }
}