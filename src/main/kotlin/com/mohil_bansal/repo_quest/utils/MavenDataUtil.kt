package com.mohil_bansal.repo_quest.utils

import org.apache.commons.lang3.StringUtils
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

object MavenDataUtil {

       private const val BASE_URL: String = "https://mvnrepository.com/search?q=commons"
        private const val ERROR_MSG: String = "Some errors occurred in the search, please submit your search content to GitHub Issue and we will fix it soon."
        private const val FAILURE_MSG: String = "The server is down. Please try again in 10 minutes"


    private fun toValue(text: String): String {
        val split = Arrays.asList(*text.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
        val sb = StringBuilder()
        for (i in 1..<split.size) {
            sb.append(split[i])
            if (i != split.size - 1) {
                sb.append("\n")
            }
        }
        return sb.toString()
    }

    fun parseInt(content: String): Int {
        var matcher: Matcher? = null
        if (StringUtils.isBlank(content)) {
            return 0
        }
        matcher = Pattern.compile("[0-9.]+").matcher(content.replace(",".toRegex(), ""))
        while (matcher.find()) {
            return matcher.group().toInt()
        }
        return 0
    }

    private fun getErrorMsg(): String {
        var msg: String = MavenDataUtil.ERROR_MSG
        try {
            msg = HttpUtils.sendGet("https://img.whalenas.com:283/file/msg.txt")
        } catch (e: Exception) {
        }
        return msg
    }
}