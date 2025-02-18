package com.mohil_bansal.repo_quest

import com.intellij.openapi.application.ex.ApplicationInfoEx
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.mohil_bansal.repo_quest.view.NpmWindow
import com.intellij.ui.content.ContentFactory
import com.mohil_bansal.repo_quest.view.MavenWindow

class MainWindow : ToolWindowFactory {

    private var mavenWindow: MavenWindow? = null
    private var npmWindow: NpmWindow? = null

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {

        val fullApplication = ApplicationInfoEx.getInstanceEx().fullApplicationName
        npmWindow = NpmWindow(project)
        mavenWindow = MavenWindow(project)

        val contentFactory = ContentFactory.getInstance()
        val npmContent = contentFactory.createContent(npmWindow!!.npmPanel, "NPM", false)
        val mavenContent = contentFactory.createContent(mavenWindow!!.mavenPanel, "Maven", false)

        if (fullApplication.startsWith("WebStorm")) {
            toolWindow.contentManager.addContent(npmContent)
            toolWindow.contentManager.addContent(mavenContent)

        } else {
            toolWindow.contentManager.addContent(mavenContent)
            toolWindow.contentManager.addContent(npmContent)
        }
    }

    override fun init(toolWindow: ToolWindow) {}

    override fun shouldBeAvailable(project: Project): Boolean {
        return true
    }
}