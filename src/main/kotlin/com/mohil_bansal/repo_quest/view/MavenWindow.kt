//Only for the testing purposes by Mohil Bansal

package com.mohil_bansal.repo_quest.view

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.DoubleClickListener
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.table.JBTable
import com.mohil_bansal.repo_quest.components.ArtifactDetail
import com.mohil_bansal.repo_quest.components.DependenceGroupItem
import com.mohil_bansal.repo_quest.components.GroupResult
import com.mohil_bansal.repo_quest.core.Callback
import com.mohil_bansal.repo_quest.models.GroupTableModel
import com.mohil_bansal.repo_quest.models.ArtifactTableModel
import com.mohil_bansal.repo_quest.utils.MavenDataUtil
import com.mohil_bansal.repo_quest.utils.NotificationUtils
import com.mohil_bansal.repo_quest.components.ArtifactItem
import com.mohil_bansal.repo_quest.utils.ArtifactDetailDialog
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

class MavenWindow(private val project: Project) {
    val mavenPanel: JPanel
    private var searchText: JBTextField
    private var searchButton: JButton
    private var prevButton: JButton
    private var nextButton: JButton
    private var currentPage: JLabel
    private var totalPage: JLabel
    private var sortSelect: JComboBox<String>
    private var repositoryTab: JPanel

    //Tables for the maven window
    private var groupTable: JBTable
    private var repoTable: JBTable
    private var groupTableModel: GroupTableModel
    private var artifactTableModel: ArtifactTableModel

    private val sortLabel = arrayOf("relevance", "popular", "newest")

    private var currentSearchText: String? = null

    private var groupTableLoading = false
    private var searchRepositoryLoading = false
    private var repositoryLoading = false

    init {
        mavenPanel = JPanel(BorderLayout())
        searchText = JBTextField()
        searchButton = JButton("Search")
        groupTable = JBTable()
        prevButton = JButton("Previous")
        nextButton = JButton("Next")
        currentPage = JLabel("1")
        totalPage = JLabel("1")
        sortSelect = ComboBox(sortLabel)
        repositoryTab = JPanel(BorderLayout())
        repoTable = JBTable()


        val model: DefaultComboBoxModel<String> = DefaultComboBoxModel(sortLabel)
        sortSelect.model = model
        groupTableModel = GroupTableModel(groupTable)
        artifactTableModel = ArtifactTableModel(repoTable)
        val sorter1 = TableRowSorter<TableModel>(groupTableModel)
        groupTable.rowSorter = sorter1
        searchText.addActionListener { handleSearch() }
        searchButton.addActionListener { handleSearch() }
        prevButton.addActionListener {
            if (!groupTableLoading) {
                var currentPageValue = currentPage.text.split("/")[0].toInt()
                if (currentPageValue > 1) {
                    currentPageValue--
                    currentPage.text = "$currentPageValue/${totalPage.text}"
                    searchGroupList()
                }
            }
        }
        nextButton.addActionListener {
            if (!groupTableLoading) {
                val totalPageValue = totalPage.text.toInt()
                var currentPageValue = currentPage.text.split("/")[0].toInt()
                if (currentPageValue < totalPageValue) {
                    currentPageValue++
                    currentPage.text = "$currentPageValue/${totalPage.text}"
                    searchGroupList()
                }
            }
        }

        handleDbClickGroupList()
        handleDbClickRepoList()

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
        mavenPanel.add(searchPanel, BorderLayout.NORTH)

        val groupScrollPane = JBScrollPane(groupTable)
        groupScrollPane.preferredSize = Dimension(800, 300)

        val paginationPanel = JPanel()
        paginationPanel.add(prevButton)
        paginationPanel.add(currentPage)
        paginationPanel.add(nextButton)

        val groupPanel = JPanel(BorderLayout())
        groupPanel.add(groupScrollPane, BorderLayout.CENTER)
        groupPanel.add(paginationPanel, BorderLayout.SOUTH)

        val repoScrollPane = JBScrollPane(repoTable)
        repoScrollPane.preferredSize = Dimension(800, 300)
        mavenPanel.add(groupPanel, BorderLayout.CENTER)
        mavenPanel.add(repoScrollPane, BorderLayout.SOUTH)
    }

    private fun handleDbClickGroupList() {
        (object : DoubleClickListener() {
            override fun onDoubleClick(event: MouseEvent): Boolean {
                if (!searchRepositoryLoading) {
                    searchRepositoryLoading = true
                    repositoryTab.removeAll()
                    val selectedRow = groupTable.selectedRow
                    val groupItem: DependenceGroupItem = groupTableModel.getData()!![selectedRow]

                    groupItem.groupLabel?.let {
                        groupItem.artifactId?.let { it1 ->
                            MavenDataUtil.getDependencies(
                                groupId = it,
                                artifactId = it1,
                                object:Callback<List<ArtifactItem?>?> {
                                    override fun onSuccess(data: List<ArtifactItem?>?) {
                                        artifactTableModel.dataVector.removeAllElements()
                                        artifactTableModel.setupTable(data)
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
                                        searchRepositoryLoading = false
                                    }
                                }
                            )
                        }
                    }
                }
                return false
            }
        }).installOn(groupTable)
    }
    private fun handleDbClickRepoList() {
        (object : DoubleClickListener() {
            override fun onDoubleClick(event: MouseEvent): Boolean {
                val selectedRow = repoTable.selectedRow
                if (selectedRow != -1) {
                    val artifactItem: ArtifactItem = artifactTableModel.data!![selectedRow]
                    MavenDataUtil.searchArtifactDetail(artifactItem, object : Callback<ArtifactDetail?> {
                        override fun onSuccess(detail: ArtifactDetail?) {
                            if (detail != null) {
                                SwingUtilities.invokeLater {
                                    val parentFrame = SwingUtilities.getWindowAncestor(repoTable) as JFrame
                                    ArtifactDetailDialog(parentFrame, detail).isVisible = true
                                }
                            } else {
                                println("Detail is null")
                            }
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
                            // Handle completion if needed
                        }
                    })
                }
                return false
            }
        }).installOn(repoTable)
    }

    private fun handleSearch() {
        if (!groupTableLoading) {
            currentPage.text = "1/1"
            totalPage.text = "1"
            currentSearchText = searchText.text
            searchGroupList()
        }
    }

    private fun searchGroupList() {
        if (!StringUtil.isEmpty(currentSearchText)) {
            val currentPageText = currentPage.text.split("/")[0]
            val pageValue = (currentPageText.toInt()).toString()
            val sortText = sortLabel[sortSelect.selectedIndex]
            groupTableLoading = true
            groupTable.setPaintBusy(true)
            MavenDataUtil.searchGroupList(
                currentSearchText!!,
                pageValue,
                sortText,
                object : Callback<GroupResult?> {
                    override fun onSuccess(result: GroupResult?) {
                        if (result != null) {
                            val list: List<DependenceGroupItem> = result.data ?: emptyList()
                            val totalPageValue: Int = result.totalPage
                            val maxPages = 100 // Limit the total number of pages to 100
                            totalPage.text = "${ceil((totalPageValue / 10.0)).toInt().coerceAtMost(maxPages)}"
                            currentPage.text = "${currentPage.text.split("/")[0]}/${totalPage.text}"
                            groupTableModel.dataVector.removeAllElements()
                            groupTableModel.setupTable(list)
                        }
                    }

                    override fun onFailure(msg: String?) {
                        //TODO: Handle it in a better way
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
                        groupTableLoading = false
                        groupTable.setPaintBusy(false)
                    }
                })
        }
    }
}
