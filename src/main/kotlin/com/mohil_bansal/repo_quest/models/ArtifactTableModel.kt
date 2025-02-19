package com.mohil_bansal.repo_quest.models

import com.intellij.ui.table.JBTable
import com.mohil_bansal.repo_quest.components.ArtifactItem
import org.apache.commons.lang3.StringUtils
import java.awt.Font
import java.util.*
import javax.swing.JTable
import javax.swing.JTextField
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel
import javax.swing.table.JTableHeader
import javax.swing.table.TableColumnModel

class ArtifactTableModel(private val table: JTable) : DefaultTableModel() {

    private val columnNames = arrayOf("Index", "Version", "Repository", "ArtifactId", "Usages", "Date")
    private val types = arrayOf<Class<*>>(Int::class.java, String::class.java, String::class.java, String::class.java, Int::class.java, String::class.java)
    var data: List<ArtifactItem>? = null

    init {
        table.autoResizeMode = JTable.AUTO_RESIZE_OFF
        table.rowHeight = 30
        table.model = this
        setColumnIdentifiers(columnNames)

        // Set table header style
        val tableHeader: JTableHeader = table.tableHeader
        tableHeader.font = Font(null, Font.BOLD, 12)

        val columnModel: TableColumnModel = table.columnModel
        columnModel.getColumn(0).preferredWidth = 80
        for (i in 1 until columnNames.size) {
            columnModel.getColumn(i).preferredWidth = 125
        }

        val renderer = DefaultTableCellRenderer().apply {
            horizontalAlignment = JTextField.CENTER
        }

        table.getColumn("Index").cellRenderer = renderer
    }

    private fun convertData(artifactItem: ArtifactItem): Vector<Any> {
        return Vector<Any>(columnNames.size).apply {
            columnNames.forEach { columnName ->
                addElement(artifactItem.getValueByColumn(columnName))
            }
        }
    }

    fun setupTable(list: List<ArtifactItem?>?) {
        data = (list ?: emptyList()) as List<ArtifactItem>?
        list?.forEachIndexed { index, item ->
            if (item != null) {
                updateData(item, index)
            }
        }
    }

    private fun updateData(artifactItem: ArtifactItem, index: Int) {
        val convertData = convertData(artifactItem).apply {
            this[0] = index + 1
        }
        addRow(convertData)
    }

    fun setStriped(striped: Boolean) {
        if (table is JBTable) {
            (table as JBTable).isStriped = striped
        } else {
            throw RuntimeException("table is not of type JBTable, please implement setStriped yourself")
        }
    }

    override fun isCellEditable(row: Int, column: Int): Boolean = false

    override fun getColumnClass(columnIndex: Int): Class<*> = types[columnIndex]
}