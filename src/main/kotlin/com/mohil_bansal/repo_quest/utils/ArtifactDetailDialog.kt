package com.mohil_bansal.repo_quest.utils

import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTabbedPane
import javax.swing.*
import java.awt.*
import com.mohil_bansal.repo_quest.components.ArtifactDetail
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.border.EmptyBorder

class ArtifactDetailDialog(parent: JFrame, artifactDetail: ArtifactDetail) : JDialog(parent, "Artifact Details", true) {
    init {
        layout = BorderLayout()
        val tabbedPane = JBTabbedPane()

        artifactDetail.gradleKotlinContent?.let {
            tabbedPane.addTab("Gradle Kotlin", createCopyableTextArea(it))
        }
        artifactDetail.mavenContent?.let {
            tabbedPane.addTab("Maven", createCopyableTextArea(it))
        }
        artifactDetail.gradleContent?.let {
            tabbedPane.addTab("Gradle", createCopyableTextArea(it))
        }
        artifactDetail.sbtContent?.let {
            tabbedPane.addTab("SBT", createCopyableTextArea(it))
        }
        artifactDetail.ivyContent?.let {
            tabbedPane.addTab("Ivy", createCopyableTextArea(it))
        }
        artifactDetail.leiningenContent?.let {
            tabbedPane.addTab("Leiningen", createCopyableTextArea(it))
        }
        artifactDetail.grapeContent?.let {
            tabbedPane.addTab("Grape", createCopyableTextArea(it))
        }
        artifactDetail.buildrContent?.let {
            tabbedPane.addTab("Buildr", createCopyableTextArea(it))
        }

        val detailPanel = JPanel(GridLayout(0, 2, 10, 10))
        detailPanel.border = EmptyBorder(10, 10, 10, 10)
        detailPanel.add(JLabel("Artifact ID:"))
        detailPanel.add(JLabel(artifactDetail.artifactId))
        detailPanel.add(JLabel("Version:"))
        detailPanel.add(JLabel(artifactDetail.version))
        detailPanel.add(JLabel("License:"))
        detailPanel.add(JLabel(artifactDetail.license))
        detailPanel.add(JLabel("Categories:"))
        detailPanel.add(JLabel(artifactDetail.category))
        detailPanel.add(JLabel("Organization:"))
        detailPanel.add(JLabel(artifactDetail.organization))
        detailPanel.add(JLabel("HomePage:"))
        detailPanel.add(JLabel(artifactDetail.homePage))
        detailPanel.add(JLabel("Date:"))
        detailPanel.add(JLabel(artifactDetail.date))
        detailPanel.add(JLabel("Repositories:"))
        detailPanel.add(JLabel(artifactDetail.repository))

        add(tabbedPane, BorderLayout.CENTER)
        add(detailPanel, BorderLayout.NORTH)

        val closeButton = JButton("Close")
        closeButton.addActionListener { dispose() }
        add(closeButton, BorderLayout.SOUTH)

        preferredSize = Dimension(515, 500)
        pack()
        setLocationRelativeTo(parent)
    }

    private fun createCopyableTextArea(content: String): JScrollPane {
        val textArea = JTextArea(content)
        textArea.isEditable = false
        textArea.lineWrap = true
        textArea.wrapStyleWord = true
        textArea.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                ClipboardUtil.setClipboardString(content)
                showCopyNotification(content)
            }
        })

        return JBScrollPane(textArea)
    }


    private fun showCopyNotification(content: String) {
        val notification = JOptionPane()
        val dialog = notification.createDialog(this, "Copied")
        notification.message = "$content has been copied to clipboard"
        notification.messageType = JOptionPane.INFORMATION_MESSAGE
        dialog.isModal = false
        dialog.isVisible = true
//        dialog.setSize(300, 150)
        dialog.pack()
        Timer(1500) { dialog.dispose() }.start()

    }
}