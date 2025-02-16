package com.mohil_bansal.repo_quest.view

import com.intellij.ui.components.JBLabel
import com.mohil_bansal.repo_quest.components.ArtifactDetail
import com.mohil_bansal.repo_quest.utils.ClipboardUtil
import java.awt.Toolkit
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JPanel
import javax.swing.JTextArea

class DetailDialog : JDialog() {
    private val contentPane: JPanel = JPanel()
    private val artifactId: JBLabel = JBLabel()
    private val mavenContent: JTextArea = JTextArea()
    private val gradleContent: JTextArea = JTextArea()
    private val versionContent: JBLabel = JBLabel()
    private val licenseContent: JBLabel = JBLabel()
    private val categoryContent: JBLabel = JBLabel()
    private val organizationContent: JBLabel = JBLabel()
    private val homePageContent: JBLabel = JBLabel()
    private val dateContent: JBLabel = JBLabel()
    private val repositoryContent: JBLabel = JBLabel()
    var copyMaven: JButton = JButton("Copy Maven")
    var copyGradle: JButton = JButton("Copy Gradle")

    init {
        // Initialize the dialog layout and add components
        contentPane.layout = null
        setContentPane(contentPane)
        isModal = true

        // Add components to the content pane
        contentPane.add(artifactId)
        contentPane.add(mavenContent)
        contentPane.add(gradleContent)
        contentPane.add(versionContent)
        contentPane.add(licenseContent)
        contentPane.add(categoryContent)
        contentPane.add(organizationContent)
        contentPane.add(homePageContent)
        contentPane.add(dateContent)
        contentPane.add(repositoryContent)
        contentPane.add(copyMaven)
        contentPane.add(copyGradle)

        // Set bounds for components (example positions, adjust as needed)
        artifactId.setBounds(10, 10, 300, 20)
        versionContent.setBounds(10, 40, 300, 20)
        licenseContent.setBounds(10, 70, 300, 20)
        categoryContent.setBounds(10, 100, 300, 20)
        organizationContent.setBounds(10, 130, 300, 20)
        homePageContent.setBounds(10, 160, 300, 20)
        dateContent.setBounds(10, 190, 300, 20)
        repositoryContent.setBounds(10, 220, 300, 20)
        mavenContent.setBounds(10, 250, 300, 60)
        gradleContent.setBounds(10, 320, 300, 60)
        copyMaven.setBounds(10, 390, 150, 30)
        copyGradle.setBounds(170, 390, 150, 30)
    }

    fun setData(detail: ArtifactDetail) {
        artifactId.text = detail.artifactId
        versionContent.text = detail.version
        licenseContent.text = detail.license
        categoryContent.text = detail.category
        organizationContent.text = detail.organization
        homePageContent.text = detail.homePage
        dateContent.text = detail.date
        repositoryContent.text = detail.repository
        mavenContent.text = detail.mavenContent
        gradleContent.text = detail.gradleContent

        copyMaven.addActionListener { e: ActionEvent? -> ClipboardUtil.setClipboardString(mavenContent.text) }
        copyGradle.addActionListener { e: ActionEvent? -> ClipboardUtil.setClipboardString(gradleContent.text) }
    }

    fun showDialog(width: Int, height: Int, isInCenter: Boolean, isResizable: Boolean) {
        pack()
        this.isResizable = isResizable
        setSize(width, height)
        if (isInCenter) {
            setLocation(
                Toolkit.getDefaultToolkit().screenSize.width / 2 - width / 2,
                Toolkit.getDefaultToolkit().screenSize.height / 2 - height / 2
            )
        }
        isVisible = true
    }
}