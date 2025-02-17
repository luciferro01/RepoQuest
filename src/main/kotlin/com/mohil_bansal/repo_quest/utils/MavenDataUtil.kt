//package com.mohil_bansal.repo_quest.utils
//
//import com.intellij.openapi.application.ApplicationManager
//import com.mohil_bansal.repo_quest.components.DependenceGroupItem
//import com.mohil_bansal.repo_quest.components.RepositoryItem
//import com.mohil_bansal.repo_quest.core.Callback
//import com.mohil_bansal.repo_quest.components.GroupResult
//import com.mohil_bansal.repo_quest.components.ArtifactItem
//import com.mohil_bansal.repo_quest.components.ArtifactDetail
//import com.mohil_bansal.repo_quest.view.ArtifactTable
//import org.apache.commons.lang3.StringUtils
//import org.jsoup.Jsoup
//import org.jsoup.nodes.Element
//import org.jsoup.select.Elements
//import java.util.*
//import java.util.function.Consumer
//import java.util.regex.Matcher
//import java.util.regex.Pattern
////git commit --date="yesterday" -m"chore: Adding User Agent for the HttpUtils"
//
//object MavenDataUtil {
//
//       private const val BASE_URL: String = "https://mvnrepository.com"
//        private const val ERROR_MSG: String = "Some errors occurred in the search, please submit your search content to GitHub Issue and we will fix it soon."
//        private const val FAILURE_MSG: String = "The server is down. Please try again in 10 minutes"
//
//
//    fun searchMvn(param : String?){
//        try {
//            val result: String = HttpUtils.sendGet(BASE_URL + "/search?q=" + param)
//            if (!result.contains("Cloudflare")) {
//                print(result)
//                val document = Jsoup.parse(result)
//                val snippets = document.getElementById("snippets")
//                val repositoryTabs = snippets.getElementsByClass("tabs")[0]
//                val elements = repositoryTabs.getElementsByTag("a")
//                val repositoryList: MutableList<RepositoryItem> =
//                    ArrayList<RepositoryItem>()
//                elements.forEach(Consumer<Element> { element: Element ->
//                    val item: RepositoryItem = RepositoryItem()
//                    item.path = element.attr("href")
//                    item.title = element.text()
//                    repositoryList.add(item)
//                })
//            }
//
//        }catch(e: Exception){
//            e.printStackTrace()
//        }
//    }
//
//    fun searchRepositoryList(groupItem: DependenceGroupItem, callback: Callback<List<RepositoryItem?>?>) {
//        val artifactId: String = groupItem.artifactId.toString()
//        val groupId: String = groupItem.groupLabel.toString()
//        ApplicationManager.getApplication().executeOnPooledThread {
//            try {
//                val result: String =
//                    HttpUtils.sendGet(BASE_URL + "/artifact/" + groupId + "/" + artifactId)
//                if (!result.contains("Cloudflare")) {
//                    val document = Jsoup.parse(result)
//                    val snippets = document.getElementById("snippets")
//                    val repositoryTabs = snippets.getElementsByClass("tabs")[0]
//                    val elements = repositoryTabs.getElementsByTag("a")
//                    val repositoryList: MutableList<RepositoryItem> =
//                        ArrayList<RepositoryItem>()
//                    elements.forEach(Consumer<Element> { element: Element ->
//                        val item: RepositoryItem = RepositoryItem()
//                        item.path = element.attr("href")
//                        item.title = element.text()
//                        repositoryList.add(item)
//                    })
//                    callback.onSuccess(repositoryList)
//                } else {
//                    callback.onFailure(FAILURE_MSG)
//                }
//            } catch (e1: java.lang.Exception) {
//                e1.printStackTrace()
//                callback.onError(getErrorMsg())
//            } finally {
//                callback.onComplete()
//            }
//        }
//    }
//
//    fun searchGroupList(value: String, currentPage: String, sortText: String, callback: Callback<GroupResult?>) {
//        ApplicationManager.getApplication().executeOnPooledThread {
//            try {
//                val result: String =
//                    HttpUtils.sendGet(BASE_URL + "/search?q=" + value + "&p=" + currentPage + "&sort=" + sortText)
//                // 如果返回了"On more step"认证信息，则提示用户过一会再试
//                if (!result.contains("Cloudflare")) {
//                    val document = Jsoup.parse(result)
//                    // 获取分页信息
//                    val totalPage = document.getElementById("maincontent").child(0).child(0).text()
//                    // 获取GroupList信息
//                    val dependenceDom = document.getElementsByClass("im-header")
//                    val list: MutableList<DependenceGroupItem> =
//                        ArrayList<DependenceGroupItem>()
//                    dependenceDom.forEach(Consumer<Element> { item: Element ->
//                        val titleDom = item.getElementsByClass("im-title").first()
//                        val subTitleDom = item.getElementsByClass("im-subtitle").first()
//                        val artifactTitle = titleDom.getElementsByTag("a").first()
//                        val usage = titleDom.getElementsByClass("im-usage").first()
//                        val subTitleArray = subTitleDom.getElementsByTag("a")
//                        val groupId = subTitleArray.first()
//                        val artifactId = subTitleArray[1]
//                        val groupItem: DependenceGroupItem = DependenceGroupItem()
//                        groupItem.artifactLabel = artifactTitle.text()
//                        groupItem.groupLabel = groupId.text()
//                        groupItem.usagesLabel =
//                            usage?.text()?.split(" ".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
//                                ?.get(0) ?: "0"
//
//                        groupItem.artifactId = artifactId.text()
//                        list.add(groupItem)
//                    })
//                    val data: GroupResult = GroupResult()
//                    data.data = list
//                    data.totalPage = totalPage.toInt()
//                    callback.onSuccess(data)
//                } else {
//                    callback.onFailure(FAILURE_MSG)
//                }
//            } catch (e: java.lang.Exception) {
//                e.printStackTrace()
//                callback.onError(getErrorMsg())
//            } finally {
//                callback.onComplete()
//            }
//        }
//    }
//
//    fun searchArtifactList(artifactTable: ArtifactTable, callback: Callback<List<ArtifactItem?>?>) {
//        val groupItem: DependenceGroupItem = artifactTable.getGroupItem()
//        val repositoryPath: String = artifactTable.repositoryPath
//        val artifactId: String = groupItem.artifactId.toString()
//        val groupId: String = groupItem.groupLabel.toString()
//        ApplicationManager.getApplication().executeOnPooledThread {
//            try {
//                val result: String = HttpUtils.sendGet(BASE_URL + repositoryPath)
//                if (!result.contains("Cloudflare")) {
//                    val document = Jsoup.parse(result)
//                    val versionTable = document.getElementsByClass("grid versions")[0]
//                    val tbodyList = versionTable.getElementsByTag("tbody")
//                    val thList = versionTable.getElementsByTag("th")
//                    val list: MutableList<ArtifactItem> = ArrayList<ArtifactItem>()
//                    tbodyList.forEach(Consumer<Element> { tbody: Element ->
//                        val trList = tbody.children()
//                        trList.forEach(Consumer<Element> { tr: Element ->
//                            val tdList = tr.children()
//                            var offset = 0
//                            if (tdList.size < thList.size) {
//                                offset = -1
//                            }
//                            val artifactItem: ArtifactItem = ArtifactItem()
//                            val versionText = tdList[getIndexFromThList(thList, "Version") + offset].text()
//                            val repositoryText = tdList[getIndexFromThList(thList, "Repository") + offset].text()
//                            val usagesText = tdList[getIndexFromThList(thList, "Usages") + offset].text()
//                            val offsetText = tdList[getIndexFromThList(thList, "Date") + offset].text()
//                            artifactItem.version = (versionText)
//                            artifactItem.repository = (repositoryText)
//                            artifactItem.usages = (usagesText)
//                            artifactItem.date =(offsetText)
//                            artifactItem.groupId =(groupId)
//                            artifactItem.artifactId = (artifactId)
//                            list.add(artifactItem)
//                        })
//                    })
//                    callback.onSuccess(list)
//                } else {
//                    callback.onFailure(FAILURE_MSG)
//                }
//            } catch (e1: java.lang.Exception) {
//                e1.printStackTrace()
//                callback.onError(getErrorMsg())
//            } finally {
//                callback.onComplete()
//            }
//        }
//    }
//
//    private fun getIndexFromThList(thList: Elements, thName: String): Int {
//        for (i in thList.indices) {
//            if (thName == thList[i].text()) {
//                return i
//            }
//        }
//        return 0
//    }
//
//    fun searchArtifactDetail(artifactItem: ArtifactItem, callback: Callback<ArtifactDetail?>) {
//        ApplicationManager.getApplication().executeOnPooledThread {
//            try {
//                val result: String =
//                    HttpUtils.sendGet(BASE_URL + "/artifact/" + artifactItem.groupId + "/" + artifactItem.artifactId + "/" + artifactItem.version)
//                if (!result.contains("Cloudflare")) {
//                    val document = Jsoup.parse(result)
//                    val table = document.getElementsByTag("table")[0]
//                    val trList = table.getElementsByTag("tr")
//                    val maven = document.getElementById("maven-a")
//                    val gradle = document.getElementById("gradle-a")
//                    val detail: ArtifactDetail = ArtifactDetail()
//                    detail.artifactId = (artifactItem.artifactId)
//                    detail.version = (artifactItem.version)
//                    detail.mavenContent = (toValue(maven.text()))
//                    detail.gradleContent =(toValue(gradle.text()))
//                    trList.forEach(Consumer { tr: Element ->
//                        val key = tr.child(0).text()
//                        val value = tr.child(1).text()
//                        if ("License" == key) {
//                            detail.license = (value)
//                        } else if ("Categories" == key) {
//                            detail.category = (value)
//                        } else if ("Organization" == key) {
//                            detail.organization = (value)
//                        } else if ("HomePage" == key) {
//                            detail.homePage = (value)
//                        } else if ("Date" == key) {
//                            detail.date = (value)
//                        } else if ("Repositories" == key) {
//                            detail.repository = (value)
//                        }
//                    })
//                    callback.onSuccess(detail)
//                } else {
//                    callback.onFailure(FAILURE_MSG)
//                }
//            } catch (e1: java.lang.Exception) {
//                e1.printStackTrace()
//                callback.onError(getErrorMsg())
//            } finally {
//                callback.onComplete()
//            }
//        }
//    }
//
//
//    private fun toValue(text: String): String {
//        val split = Arrays.asList(*text.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
//        val sb = StringBuilder()
//        for (i in 1..<split.size) {
//            sb.append(split[i])
//            if (i != split.size - 1) {
//                sb.append("\n")
//            }
//        }
//        return sb.toString()
//    }
//
//    fun parseInt(content: String): Int {
//        var matcher: Matcher? = null
//        if (StringUtils.isBlank(content)) {
//            return 0
//        }
//        matcher = Pattern.compile("[0-9.]+").matcher(content.replace(",".toRegex(), ""))
//        while (matcher.find()) {
//            return matcher.group().toInt()
//        }
//        return 0
//    }
//
//    private fun getErrorMsg(): String {
//        var msg: String = MavenDataUtil.ERROR_MSG
//        try {
//            msg = HttpUtils.sendGet("https://img.whalenas.com:283/file/msg.txt")
//        } catch (e: Exception) {
//        }
//        return msg
//    }
//}

package com.mohil_bansal.repo_quest.utils

import com.intellij.openapi.application.ApplicationManager
import com.mohil_bansal.repo_quest.components.DependenceGroupItem
import com.mohil_bansal.repo_quest.components.RepositoryItem
import com.mohil_bansal.repo_quest.core.Callback
import com.mohil_bansal.repo_quest.components.GroupResult
import com.mohil_bansal.repo_quest.components.ArtifactItem
import com.mohil_bansal.repo_quest.components.ArtifactDetail
import com.mohil_bansal.repo_quest.view.ArtifactTable
import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.IOException
import java.util.*
import java.util.regex.Pattern
import java.util.regex.Matcher
import kotlin.collections.ArrayList

object MavenDataUtil {

    private const val BASE_URL: String = "https://mvnrepository.com"
    private const val ERROR_MSG: String =
        "Some errors occurred in the search, please submit your search content to GitHub Issue and we will fix it soon."
    private const val FAILURE_MSG: String = "The server is down. Please try again in 10 minutes"


    fun searchMvn(param: String?) {
        try {
            val searchUrl = "$BASE_URL/search?q=$param"
            val result = HttpUtils.sendGet(searchUrl)
            if (!result.contains("Cloudflare")) {
                val document = Jsoup.parse(result)

                val searchResults = document.getElementsByClass("im-subtitle")

                val artifacts: MutableList<RepositoryItem> = ArrayList()
                for (element in searchResults) {
                    val hrefs = element.getElementsByTag("a")
                    if (hrefs.size >= 2) {
                        val artifact = RepositoryItem().apply {
                            title = hrefs[0].text()
                            path = hrefs[1].text()
                            // In this case "path" seems incorrect, I assume it is artifactId
                            // You can adjust this logic if "path" should be something different
                            // e.g.  path = "/artifact/"+ hrefs[0].text() + "/" + hrefs[1].text();

                        }
                        artifacts.add(artifact)
                        println("searchMvn Artifact: Title=${artifact.title}, Path=${artifact.path}") // Print data
                    }
                }

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun searchRepositoryList(groupItem: DependenceGroupItem, callback: Callback<List<RepositoryItem?>?>) {
        val artifactId: String = groupItem.artifactId.toString()
        val groupId: String = groupItem.groupLabel.toString()
        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                val result = HttpUtils.sendGet("$BASE_URL/artifact/$groupId/$artifactId")
                if (!result.contains("Cloudflare")) {
                    val document = Jsoup.parse(result)
                    val tabElements = document.select("ul.nav.nav-tabs li a")

                    val repositoryList: MutableList<RepositoryItem> = ArrayList()

                    for (element in tabElements) {
                        val item = RepositoryItem().apply {
                            title = element.text()
                            path = element.attr("href")
                        }
                        repositoryList.add(item)
                        println("searchRepositoryList Item: Title=${item.title}, Path=${item.path}")

                    }
                    callback.onSuccess(repositoryList)

                } else {
                    callback.onFailure(FAILURE_MSG)
                }
            } catch (e1: java.lang.Exception) {
                e1.printStackTrace()
                callback.onError(getErrorMsg())
            } finally {
                callback.onComplete()
            }
        }
    }

    fun searchGroupList(
        value: String,
        currentPage: String,
        sortText: String,
        callback: Callback<GroupResult?>
    ) {
        ApplicationManager.getApplication().executeOnPooledThread {


            try {
                val result = if(currentPage == "0"){
                    HttpUtils.sendGet("$BASE_URL/search?q=$value&sort=$sortText")
                } else {
                    HttpUtils.sendGet("$BASE_URL/search?q=$value&p=$currentPage&sort=$sortText")
                }
                if (!result.contains("Cloudflare")) {
                    val document = Jsoup.parse(result)

                    // Extract total page count
                    val mainContent = document.getElementById("maincontent")
                    val totalPageText = mainContent?.children()?.getOrNull(0)?.children()?.getOrNull(0)?.text()
                    val totalPage = totalPageText?.toIntOrNull() ?: 0

                    // Extract group items
                    val dependenceDom = document.getElementsByClass("im-header")
                    val list: MutableList<DependenceGroupItem> = ArrayList()

                    for (item in dependenceDom) {
                        val titleDom = item.getElementsByClass("im-title").firstOrNull()
                        val subTitleDom = item.getElementsByClass("im-subtitle").firstOrNull()
                        val artifactTitle = titleDom?.getElementsByTag("a")?.firstOrNull()
                        val usage = titleDom?.getElementsByClass("im-usage")?.firstOrNull()
                        val subTitleArray = subTitleDom?.getElementsByTag("a")

                        val groupId = subTitleArray?.getOrNull(0)
                        val artifactId = subTitleArray?.getOrNull(1)

                        if (artifactTitle != null && groupId != null && artifactId != null) {
                            val groupItem = DependenceGroupItem().apply {
                                artifactLabel = artifactTitle.text()
                                groupLabel = groupId.text()
                                usagesLabel = usage?.text()?.split(" ".toRegex())
                                    ?.dropLastWhile { it.isEmpty() }
                                    ?.toTypedArray()
                                    ?.getOrNull(0) ?: "0" // Default to "0" if no usage data

                                this.artifactId = artifactId.text()
                            }
                            list.add(groupItem)
                            println("searchGroupList Item: ArtifactLabel=${groupItem.artifactLabel}, GroupLabel=${groupItem.groupLabel}, ArtifactId=${groupItem.artifactId}")
                        }
                    }

                    val data = GroupResult().apply {
                        this.data = list
                        this.totalPage = totalPage
                    }
                    callback.onSuccess(data)
                } else {
                    callback.onFailure(FAILURE_MSG)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback.onError(getErrorMsg())
            } finally {
                callback.onComplete()
            }
        }
    }

    fun searchArtifactList(artifactTable: ArtifactTable, callback: Callback<List<ArtifactItem?>?>) {
        val groupItem: DependenceGroupItem = artifactTable.getGroupItem()
        val repositoryPath: String = artifactTable.repositoryPath
        val artifactId: String = groupItem.artifactId.toString()
        val groupId: String = groupItem.groupLabel.toString()
        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                val result = HttpUtils.sendGet(BASE_URL + repositoryPath)
                if (!result.contains("Cloudflare")) {
                    val document = Jsoup.parse(result)
                    val versionTable = document.getElementsByClass("grid versions").firstOrNull()
                    if (versionTable != null) {
                        val tbodyList = versionTable.getElementsByTag("tbody")
                        val thList = versionTable.getElementsByTag("th")
                        val list: MutableList<ArtifactItem> = ArrayList()
                        for (tbody in tbodyList) {
                            val trList = tbody.children()
                            for (tr in trList) {
                                val tdList = tr.children()
                                var offset = 0
                                if (tdList.size < thList.size) {
                                    offset = -1
                                }

                                val versionText =
                                    tdList.getOrNull(getIndexFromThList(thList, "Version") + offset)?.text() ?: ""
                                val repositoryText =
                                    tdList.getOrNull(getIndexFromThList(thList, "Repository") + offset)?.text() ?: ""
                                val usagesText =
                                    tdList.getOrNull(getIndexFromThList(thList, "Usages") + offset)?.text() ?: ""
                                val offsetText =
                                    tdList.getOrNull(getIndexFromThList(thList, "Date") + offset)?.text() ?: ""

                                val artifactItem = ArtifactItem().apply {
                                    version = versionText
                                    repository = repositoryText
                                    usages = usagesText
                                    date = offsetText
                                    this.groupId = groupId
                                    this.artifactId = artifactId
                                }
                                list.add(artifactItem)
                                println("searchArtifactList Item: Version=${artifactItem.version}, Repository=${artifactItem.repository}, Usages=${artifactItem.usages}, Date=${artifactItem.date}")
                            }
                        }
                        callback.onSuccess(list)
                    } else {
                        callback.onError("version table not found")
                    }

                } else {
                    callback.onFailure(FAILURE_MSG)
                }
            } catch (e1: java.lang.Exception) {
                e1.printStackTrace()
                callback.onError(getErrorMsg())
            } finally {
                callback.onComplete()
            }
        }
    }

    fun getDependencies(groupId: String, artifactId: String, callback: Callback<List<ArtifactItem?>?>) {
        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                val url = String.format("$BASE_URL/artifact/%s/%s", groupId, artifactId)
                val result = HttpUtils.sendGet(url)
                if (!result.contains("Cloudflare")) {
                    val document = Jsoup.parse(result)
                    val elements = document.getElementsByClass("vbtn release")

                    val dependencies: MutableList<ArtifactItem> = ArrayList()
                    for (element in elements) {
                        val versionText = element.text()

                        val artifactItem = ArtifactItem().apply {
                            this.groupId = groupId
                            this.artifactId = artifactId
                            version = versionText
                        }
                        dependencies.add(artifactItem)
                        println("getDependencies Item: GroupId=${artifactItem.groupId}, ArtifactId=${artifactItem.artifactId}, Version=${artifactItem.version}")
                    }
                    callback.onSuccess(dependencies)
                } else {
                    callback.onFailure(FAILURE_MSG)
                }
            } catch (e1: java.lang.Exception) {
                e1.printStackTrace()
                callback.onError(getErrorMsg())
            } finally {
                callback.onComplete()
            }
        }
    }

    private fun getIndexFromThList(thList: Elements, thName: String): Int {
        for (i in thList.indices) {
            if (thName == thList[i].text()) {
                return i
            }
        }
        return 0
    }

    fun searchArtifactDetail(artifactItem: ArtifactItem, callback: Callback<ArtifactDetail?>) {
        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                val result =
                    HttpUtils.sendGet("$BASE_URL/artifact/${artifactItem.groupId}/${artifactItem.artifactId}/${artifactItem.version}")
                if (!result.contains("Cloudflare")) {
                    val document = Jsoup.parse(result)
                    val tables = document.getElementsByTag("table")
                    val table = tables.firstOrNull()

                    val maven = document.getElementById("maven-a")
                    val gradle = document.getElementById("gradle-a")

                    val detail = ArtifactDetail().apply {
                        artifactId = artifactItem.artifactId
                        version = artifactItem.version
                        mavenContent = toValue(maven?.text() ?: "")
                        gradleContent = toValue(gradle?.text() ?: "")
                        if (table != null) {
                            val trList = table.getElementsByTag("tr")
                            for (tr in trList) {
                                val key = tr.child(0).text()
                                val value = tr.child(1).text()
                                when (key) {
                                    "License" -> license = value
                                    "Categories" -> category = value
                                    "Organization" -> organization = value
                                    "HomePage" -> homePage = value
                                    "Date" -> date = value
                                    "Repositories" -> repository = value
                                }
                            }
                        }
                    }
                    println("searchArtifactDetail Detail: ArtifactId=${detail.artifactId}, Version=${detail.version}, License=${detail.license}, Categories=${detail.category}, Organization=${detail.organization}, HomePage=${detail.homePage}, Date=${detail.date}, Repositories=${detail.repository}")
                    callback.onSuccess(detail)
                } else {
                    callback.onFailure(FAILURE_MSG)
                }
            } catch (e1: java.lang.Exception) {
                e1.printStackTrace()
                callback.onError(getErrorMsg())
            } finally {
                callback.onComplete()
            }
        }
    }


    private fun toValue(text: String): String {
        val split = Arrays.asList(*text.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
        val sb = StringBuilder()
        for (i in 1..<split.size) {
            sb.append(split[i])
            if (i != split.size - 1) {
                sb.append("\n")
            }
        }
        return sb.toString()
    }

    fun parseInt(content: String): Int {
        var matcher: Matcher? = null
        if (StringUtils.isBlank(content)) {
            return 0
        }
        matcher = Pattern.compile("[0-9.]+").matcher(content.replace(",".toRegex(), ""))
        while (matcher.find()) {
            return matcher.group().toInt()
        }
        return 0
    }

    private fun getErrorMsg(): String {
        var msg: String = MavenDataUtil.ERROR_MSG
        try {
            msg = HttpUtils.sendGet("https://img.whalenas.com:283/file/msg.txt")
        } catch (e: Exception) {
        }
        return msg
    }
}