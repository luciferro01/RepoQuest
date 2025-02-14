package com.mohil_bansal.repo_quest.view

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.DoubleClickListener
import com.intellij.ui.components.JBTextField
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.table.JBTable
import com.mohil_bansal.repo_quest.components.PackageItem
import com.mohil_bansal.repo_quest.components.PackageResult
import com.mohil_bansal.repo_quest.components.VersionItem
import com.mohil_bansal.repo_quest.core.Callback
import com.mohil_bansal.repo_quest.models.PackageTableModel
import com.mohil_bansal.repo_quest.models.VersionTableModel
import com.mohil_bansal.repo_quest.utils.ClipboardUtil
import com.mohil_bansal.repo_quest.utils.NotificationUtils
import com.mohil_bansal.repo_quest.utils.NpmDataUtil
import com.mohil_bansal.repo_quest.utils.NpmDataUtil.searchPackageList
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.ActionEvent
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.table.TableModel
import javax.swing.table.TableRowSorter
import kotlin.math.ceil

class NpmWindow(private val project: Project) {
    private var versionTable: JBTable
    private var packageTable: JBTable
    private var prevButton: JButton
    private var nextButton: JButton
    private var currentPage: JLabel
    private var totalPage: JLabel
    private var searchText: JBTextField
    private var sortSelect: ComboBox<String>
    private var searchButton: JButton
    val npmPanel: JPanel
    private val packageTableModel: PackageTableModel
    private val versionTableModel: VersionTableModel
    private var currentSearchText: String? = null
    private var packageTableLoading = false
    private var versionTableLoading = false

    private val sortLabel = arrayOf("optimal", "popularity", "quality", "maintenance")

    init {
        npmPanel = JPanel(BorderLayout())
        versionTable = JBTable()
        packageTable = JBTable()
        prevButton = JButton("Previous")
        nextButton = JButton("Next")
        currentPage = JLabel("1")
        totalPage = JLabel("1")
        searchText = JBTextField()
        sortSelect = ComboBox(sortLabel)
        searchButton = JButton("Search")

        val model: DefaultComboBoxModel<String> = DefaultComboBoxModel(sortLabel)
        sortSelect.model = model
        packageTableModel = PackageTableModel(packageTable)
        versionTableModel = VersionTableModel(versionTable)
        val versionSorter = TableRowSorter<TableModel>(versionTableModel)
        versionTable.rowSorter = versionSorter
        searchText.addActionListener { e: ActionEvent? -> handleSearch() }
        searchButton.addActionListener { e: ActionEvent? -> handleSearch() }
        prevButton.addActionListener { e: ActionEvent? ->
            if (!packageTableLoading) {
                var currentPageValue = currentPage.text.toInt()
                if (currentPageValue > 1) {
                    currentPage.text = (--currentPageValue).toString()
                    searchGroupList()
                }
            }
        }
        nextButton.addActionListener { e: ActionEvent? ->
            if (!packageTableLoading) {
                val totalPageValue = totalPage.text.toInt()
                var currentPageValue = currentPage.text.toInt()
                if (currentPageValue < totalPageValue) {
                    currentPage.text = (++currentPageValue).toString()
                    searchGroupList()
                }
            }
        }
        handleDbClickPackageList()
        handleDbClickVersionList()

        // Add components to the panel
        val searchPanel = JPanel(GridBagLayout())
        val gbc = GridBagConstraints()
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.weightx = 1.0
        gbc.gridx = 0
        searchPanel.add(searchText, gbc)
        gbc.weightx = 0.0
        gbc.gridx = 1
        searchPanel.add(sortSelect, gbc)
        gbc.gridx = 2
        searchPanel.add(searchButton, gbc)
        npmPanel.add(searchPanel, BorderLayout.NORTH)

        val packageScrollPane = JScrollPane(packageTable)
        packageScrollPane.preferredSize = Dimension(800, 300)
        npmPanel.add(packageScrollPane, BorderLayout.CENTER)

        val versionScrollPane = JScrollPane(versionTable)
        versionScrollPane.preferredSize = Dimension(800, 300)
        npmPanel.add(versionScrollPane, BorderLayout.SOUTH)
    }

    private fun handleDbClickVersionList() {
        (object : DoubleClickListener() {
            override fun onDoubleClick(event: MouseEvent): Boolean {
                if (!versionTableLoading && versionTable.selectedRow >= 0) {
                    val selectedRow = versionTable.selectedRow
                    val versionItem: VersionItem = versionTableModel.getData()!![selectedRow]
                    val commandText = "npm i -S " + versionItem.packageName + "@" + versionItem.version
                    ClipboardUtil.setClipboardString(commandText)
                    NotificationUtils.infoNotify("Copy install command success.\n\n$commandText", project)
                }
                return false
            }
        }).installOn(versionTable)
    }

    private fun handleDbClickPackageList() {
        (object : DoubleClickListener() {
            override fun onDoubleClick(event: MouseEvent): Boolean {
                if (!versionTableLoading && packageTable.selectedRow >= 0) {
                    versionTable.setPaintBusy(true)
                    versionTableLoading = true
                    val selectedRow = packageTable.selectedRow
                    val packageItem: PackageItem = packageTableModel.getData()!![selectedRow]
                    NpmDataUtil.searchVersionList(packageItem, object : Callback<List<VersionItem>> {

                        override fun onSuccess(data: List<VersionItem>) {
                            versionTableModel.dataVector.removeAllElements()
                            versionTableModel.setupTable(data)
                        }

                        override fun onFailure(msg: String?) {
                            if (msg != null) {
                                NotificationUtils.errorNotify(msg, project)
                            }
                        }

                        override fun onError(msg: String?) {
                            if (msg != null) {
                                NotificationUtils.errorNotify(msg, project)
                            }
                        }

                        override fun onComplete() {
                            versionTable.setPaintBusy(false)
                            versionTableLoading = false
                        }
                    })
                }
                return false
            }
        }).installOn(packageTable)
    }

    private fun handleSearch() {
        currentPage.text = "1"
        totalPage.text = "1"
        currentSearchText = searchText.text
        searchGroupList()
    }

    private fun searchGroupList() {
        if (!StringUtil.isEmpty(currentSearchText)) {
            val currentPageText = currentPage.text
            val pageValue = (currentPageText.toInt() - 1).toString()
            val sortText = sortLabel[sortSelect.selectedIndex]
            packageTableLoading = true
            packageTable.setPaintBusy(true)
            currentSearchText?.let {
                if (sortText != null) {
                    searchPackageList(it, pageValue, sortText, object : Callback<PackageResult> {
                        override fun onSuccess(result: PackageResult) {
                            val list: List<PackageItem> = result.data ?: emptyList()
                            val totalPageValue: Int = result.totalPage
                            val maxPages = 100 // Limit the total number of pages to 100
                            totalPage.text = (ceil((totalPageValue / 10.0)).toInt().coerceAtMost(maxPages)).toString()
                            packageTableModel.dataVector.removeAllElements()
                            versionTableModel.dataVector.removeAllElements()
                            packageTableModel.setupTable(list)
                        }

                        override fun onFailure(msg: String?) {
                            if (msg != null) {
                                NotificationUtils.errorNotify(msg, project)
                            }
                        }

                        override fun onError(msg: String?) {
                            if (msg != null) {
                                NotificationUtils.errorNotify(msg, project)
                            }

                        }

                        override fun onComplete() {
                            packageTableLoading = false
                            packageTable.setPaintBusy(false)
                        }
                    })
                }
            }
        }
    }
}