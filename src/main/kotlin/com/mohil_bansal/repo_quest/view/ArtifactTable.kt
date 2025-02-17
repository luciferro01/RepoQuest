package com.mohil_bansal.repo_quest.view

import com.intellij.openapi.project.Project
import com.intellij.ui.DoubleClickListener
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.mohil_bansal.repo_quest.models.ArtifactTableModel

import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.MouseEvent
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.table.TableModel
import javax.swing.table.TableRowSorter

import com.mohil_bansal.repo_quest.components.DependenceGroupItem
import com.mohil_bansal.repo_quest.components.ArtifactItem
import com.mohil_bansal.repo_quest.core.Callback
import com.mohil_bansal.repo_quest.components.ArtifactDetail
import com.mohil_bansal.repo_quest.utils.MavenDataUtil
import com.mohil_bansal.repo_quest.utils.NotificationUtils
import org.apache.commons.collections.CollectionUtils


class ArtifactTable(
    groupItem: DependenceGroupItem,
    private val project: Project, detailDialog: DetailDialog,
    val repositoryPath: String
) :
    JPanel(false) {
    private val artifactTable = JBTable()
    private val artifactTableModel: ArtifactTableModel = ArtifactTableModel(artifactTable)
    private val groupItem: DependenceGroupItem = groupItem

    private val detailDialog: DetailDialog

    private var artifactLoading = false

    init {
        val sorter = TableRowSorter<TableModel>(artifactTableModel)
        artifactTable.rowSorter = sorter
        artifactTable.fillsViewportHeight = true

        artifactTable.preferredScrollableViewportSize = Dimension(400, 1000)

        layout = GridLayout(1, 1)
        val scrollPane = JBScrollPane(artifactTable)
        add(scrollPane)
        this.detailDialog = detailDialog

        handleDbClickArtifactList()
    }

    fun getGroupItem(): DependenceGroupItem {
        return groupItem
    }

    fun setPaintBusy(busy: Boolean) {
        artifactTable.setPaintBusy(busy)
    }

    fun setArtifactLoading(loading: Boolean) {
        artifactLoading = loading
    }

    private fun handleDbClickArtifactList() {
        (object : DoubleClickListener() {
            override fun onDoubleClick(event: MouseEvent): Boolean {
                if (!artifactLoading) {
                    artifactTable.setPaintBusy(true)
                    artifactLoading = true
                    detailDialog.dispose()
                    val selectedRow = artifactTable.selectedRow
                    val artifactItem: ArtifactItem = artifactTableModel.data!![selectedRow]
                    MavenDataUtil.searchArtifactDetail(artifactItem, object : Callback<ArtifactDetail?> {
                        override fun onSuccess(detail: ArtifactDetail?) {
                            if (detail != null) {
                                detailDialog.setData(detail)
                            }
                            detailDialog.copyMaven.addActionListener { e ->
                                NotificationUtils.infoNotify(
                                    "Copy Maven dependence success",
                                    project
                                )
                            }
                            detailDialog.copyGradle.addActionListener { e ->
                                NotificationUtils.infoNotify(
                                    "Copy Gradle dependence success",
                                    project
                                )
                            }
                            detailDialog.showDialog(650, 550, true, false)
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
                            artifactTable.setPaintBusy(false)
                            artifactLoading = false
                        }
                    })
                }
                return false
            }
        }).installOn(artifactTable)
    }

    fun removeAllElements() {
        artifactTableModel.getDataVector().removeAllElements()
    }

    fun setupTable(list: List<ArtifactItem?>?) {
         artifactTableModel.setupTable(list)
    }

    //TODO: Look after it if the ArtifactItem is nullable
//    fun setupTable(list: List<ArtifactItem?>?) {
//        artifactTableModel.setupTable(list)
//    }

    fun hasData(): Boolean {
        return !CollectionUtils.isEmpty(artifactTableModel.data)
    }
}