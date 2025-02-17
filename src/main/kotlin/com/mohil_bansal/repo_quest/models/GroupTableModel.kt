package com.mohil_bansal.repo_quest.models

import com.intellij.ui.table.JBTable
import com.mohil_bansal.repo_quest.components.DependenceGroupItem
import org.apache.commons.lang3.StringUtils
import java.awt.Font
import java.util.*
import javax.swing.JTable
import javax.swing.JTextField
import javax.swing.table.*


class GroupTableModel(private val table: JTable) : DefaultTableModel() {
    private val columnNames = arrayOf("Index", "Artifact Title", "Artifact ID", "Group ID", "Usages")

    private val types = arrayOf<Class<*>>(
        Int::class.java,
        String::class.java,
        String::class.java,
        String::class.java,
        Int::class.java
    )

    private var data: List<DependenceGroupItem>? = null

    init {
        table.autoResizeMode = JTable.AUTO_RESIZE_OFF
        table.rowHeight = 30
        table.model = this
        setColumnIdentifiers(columnNames)
        val tableHeader = table.tableHeader
        tableHeader.font = Font(null, Font.BOLD, 12)
        val columnModel = table.columnModel
        columnModel.getColumn(0).preferredWidth = 80
        columnModel.getColumn(1).preferredWidth = 200
        columnModel.getColumn(2).preferredWidth = 200
        columnModel.getColumn(3).preferredWidth = 200
        columnModel.getColumn(4).preferredWidth = 100
        val renderer = DefaultTableCellRenderer()
        renderer.horizontalAlignment = JTextField.CENTER
        table.getColumn("Index").cellRenderer = renderer
    }

    private fun convertData(groupItem: DependenceGroupItem): Vector<Any> {

        val v = Vector<Any>(columnNames.size)
        for (columnName in columnNames) {
            v.addElement(groupItem.getValueByColumn(columnName))
        }
        return v
    }


    protected fun findRowIndex(columnIndex: Int, value: String?): Int {
        val rowCount = rowCount
        for (rowIndex in 0..<rowCount) {
            val valueAt = getValueAt(rowIndex, columnIndex)
            if (StringUtils.equalsIgnoreCase(value, valueAt.toString())) {
                return rowIndex
            }
        }
        return -1
    }


    protected fun updateRow(rowIndex: Int, rowData: Vector<Any?>?) {
        dataVector[rowIndex] = rowData

        fireTableRowsUpdated(rowIndex, rowIndex)
    }


    fun setupTable(list: List<DependenceGroupItem>) {
        this.data = list
        for (i in list.indices) {
            updateData(list[i], i)
        }
    }

    private fun updateData(groupItem: DependenceGroupItem, index: Int) {
        val convertData = convertData(groupItem)
        convertData[0] = index + 1
        addRow(convertData)
    }


    fun setStriped(striped: Boolean) {
        if (table is JBTable) {
            table.isStriped = striped
        } else {
            throw RuntimeException("The table is not of type JBTable, please implement setStriped yourself")
        }
    }

    override fun isCellEditable(row: Int, column: Int): Boolean {
        return false
    }

    fun getData(): List<DependenceGroupItem>? {
        return data
    }

    override fun getColumnClass(columnIndex: Int): Class<*> {
        return types[columnIndex]
    }
}