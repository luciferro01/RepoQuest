package com.mohil_bansal.repo_quest.utils

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable


object ClipboardUtil {
    fun setClipboardString(text: String?) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val trans: Transferable = StringSelection(text)
        clipboard.setContents(trans, null)
    }
}