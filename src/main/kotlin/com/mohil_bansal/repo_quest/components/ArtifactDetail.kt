package com.mohil_bansal.repo_quest.components

class ArtifactDetail {
    var artifactId: String? = null
        get() = if (field == null) "" else field
    var version: String? = null
        get() = if (field == null) "" else field
    var license: String? = null
        get() = if (field == null) "" else field
    var category: String? = null
        get() = if (field == null) "" else field
    var organization: String? = null
        get() = if (field == null) "" else field
    var homePage: String? = null
        get() = if (field == null) "" else field
    var date: String? = null
        get() = if (field == null) "" else field
    var repository: String? = null
        get() = if (field == null) "" else field
    var mavenContent: String? = null
    var gradleContent: String? = null
}