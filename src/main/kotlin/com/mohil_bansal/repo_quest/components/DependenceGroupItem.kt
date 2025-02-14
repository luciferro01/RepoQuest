package com.mohil_bansal.repo_quest.components

import com.mohil_bansal.repo_quest.utils.MavenDataUtil
import javax.swing.ImageIcon


class DependenceGroupItem {
    var imageIcon: ImageIcon? = null
    var artifactLabel: String? = null
    var artifactId: String? = null
    var groupLabel: String? = null
    var usagesLabel: String? = null

    fun getValueByColumn(columnName: String): Any? {
        return when (columnName) {
            "Image" -> imageIcon
            "Group ID" -> groupLabel
            "Artifact Title" -> artifactLabel
            "Artifact ID" -> artifactId
            "Usages" -> usagesLabel?.let { MavenDataUtil.parseInt(it) }
            else -> ""
        }
    }
}