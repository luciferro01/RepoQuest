package com.mohil_bansal.repo_quest.utils

import com.intellij.openapi.application.ApplicationManager
import com.mohil_bansal.repo_quest.components.PackageItem
import com.mohil_bansal.repo_quest.components.PackageResult
import com.mohil_bansal.repo_quest.components.VersionItem
import com.mohil_bansal.repo_quest.core.Callback

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

object NpmDataUtil {

    private const val BASE_URL = "https://www.npmjs.com"
    private const val ERROR_MSG =
        "Some errors occurred in the search, please submit your search content to GitHub Issue and we will fix it soon."

    @JvmStatic
    fun searchPackageList(value: String, currentPage: String, sortText: String, callback: Callback<PackageResult>) {
        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                val result = HttpUtils.sendGet("$BASE_URL/search?q=$value&page=$currentPage&ranking=$sortText")
                val document: Document = Jsoup.parse(result)
                val packageElements: Elements = document.getElementsByTag("section")
                val list = mutableListOf<PackageItem>()
                packageElements.forEach { item ->
                    val packageElement = item.child(0)
                    val packageName = packageElement.child(0).child(0).text()
                    val description = packageElement.child(2).text()
                    val author = packageElement.child(packageElement.childNodeSize() - 1).child(0).text()
                    val lastVersion =
                        packageElement.child(packageElement.childNodeSize() - 1).child(1).text().split(" ")[1]
                    val packageItem = PackageItem().apply {
                        this.packageName = packageName
                        this.description = description
                        this.author = author
                        this.lastVersion = lastVersion
                    }
                    list.add(packageItem)
                }
                val totalPageText = document.getElementById("main").child(0).getElementsByTag("h2")[0].text().split(" ")[0]
                val totalPage = if (totalPageText.contains("+")) {
                    1000 // Handle the case where total pages are more than 1000
                } else {
                    totalPageText.toInt()
                }
                val packageResult = PackageResult().apply {
                    this.totalPage = totalPage
                    this.data = list
                }
                callback.onSuccess(packageResult)
            } catch (e: Exception) {
                e.printStackTrace()
                callback.onError(ERROR_MSG)
            } finally {
                callback.onComplete()
            }
        }
    }

    @JvmStatic
    fun searchVersionList(packageItem: PackageItem, callback: Callback<List<VersionItem>>) {
        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                val result = HttpUtils.sendGet("$BASE_URL/package/${packageItem.packageName}?activeTab=versions")
                val document: Document = Jsoup.parse(result)

                //TODO: Fix this Null Prone code :) later (not in the mood rn)
                val child: Element = document.getElementById("tabpanel-versions").child(0)
                val versionElement = child.child(child.childNodeSize() - 1)
                val versionList: Elements = versionElement.getElementsByTag("tbody")[0].getElementsByTag("tr")
                val list = mutableListOf<VersionItem>()
                for (i in 1 until versionList.size) {
                    val versionItem = versionList[i]
                    val children: Elements = versionItem.children()
                    if (children.size == 3) {
                        val version = children[0].text()
                        var downloads = children[1].text().replace(",", "")
                        val published = children[2].text()
                        val item = VersionItem().apply {
                            this.version = version
                            this.downloads = downloads.toInt()
                            this.published = published
                            this.packageName = packageItem.packageName
                        }
                        list.add(item)
                    }
                }
                callback.onSuccess(list)
            } catch (e: Exception) {
                e.printStackTrace()
                callback.onError(ERROR_MSG)
            } finally {
                callback.onComplete()
            }
        }
    }
}