package com.mohil_bansal.repo_quest.utils

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project

object NotificationUtils {
    private val notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup("RepoQuest.Notification")

    fun warnNotify(message: String, project: Project?) {
        Notifications.Bus.notify(
            notificationGroup.createNotification(
                message,
                NotificationType.WARNING
            ), project
        )
    }

    fun infoNotify(message: String, project: Project?) {
        Notifications.Bus.notify(
            notificationGroup.createNotification(
                message,
                NotificationType.INFORMATION
            ), project
        )
    }

    fun errorNotify(message: String, project: Project?) {
        Notifications.Bus.notify(
            notificationGroup.createNotification(
                message,
                NotificationType.ERROR
            ), project
        )
    }
}