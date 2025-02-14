package com.mohil_bansal.repo_quest.components

import com.mohil_bansal.repo_quest.components.DependenceGroupItem

class GroupResult {

    var data: List<DependenceGroupItem>? = null
    var totalPage: Int = 0

    fun getValueByColumn(columnName: String): Any {
        return when (columnName) {
            "data" -> data ?: ""
            "totalPage" -> totalPage
            else -> ""
        }
    }
}