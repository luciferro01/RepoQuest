package com.mohil_bansal.repo_quest

import com.intellij.openapi.application.ex.ApplicationInfoEx
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.mohil_bansal.repo_quest.view.NpmWindow
import javax.swing.JPanel
import com.intellij.ui.content.ContentFactory

class MainWindow : ToolWindowFactory {

    private val mainPanel: JPanel? = null
    private var npmWindow: NpmWindow? = null

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {

        val fullApplication = ApplicationInfoEx.getInstanceEx().fullApplicationName
        npmWindow = NpmWindow(project)
        val contentFactory = ContentFactory.getInstance()
        val npmContent = contentFactory.createContent(npmWindow!!.npmPanel, "NPM", false)


        toolWindow.contentManager.addContent(npmContent)
//        if (fullApplication.startsWith("WebStorm")) {
//            toolWindow.contentManager.addContent(npmContent)
//        } else {
//            toolWindow.contentManager.addContent(npmContent)
//        }
    }

    override fun init(toolWindow: ToolWindow) {}

    override fun shouldBeAvailable(project: Project): Boolean {
        return true
    }
}