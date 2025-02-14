package com.mohil_bansal.repo_quest.components

class VersionItem {
    var version: String? = null
    var downloads: Int? = null
    var published: String? = null
    var packageName: String? = null

    fun getValueByColumn(columnName: String): Any? {
        return when (columnName) {
            "Version" -> version
            "Weekly Downloads" -> downloads
            "Published" -> published
            "Package Name" -> packageName
            else -> ""
        }
    }
}