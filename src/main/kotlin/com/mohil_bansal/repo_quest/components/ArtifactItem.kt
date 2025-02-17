package com.mohil_bansal.repo_quest.components

import com.mohil_bansal.repo_quest.utils.MavenDataUtil
class ArtifactItem {
    var version: String? = null
    var repository: String? = null
    var usages: String? = null
    var date: String? = null
    var groupId: String? = null
    var artifactId: String? = null

    fun getValueByColumn(columnName: String): Any? {
        return when (columnName) {
            "Version" -> version
            "Repository" -> repository
            "Usages" -> usages?.let { MavenDataUtil.parseInt(it) }
            "Date" -> date
            "GroupId" -> groupId
            "ArtifactId" -> artifactId
            else -> ""
        }
    }
}