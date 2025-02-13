
package com.mohil_bansal.repo_quest.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

object HttpUtils {

    private val client = OkHttpClient()

    @JvmStatic
    @Throws(IOException::class)
    fun sendGet(url: String): String {
        return sendGet(url, emptyMap())
    }

    /**
     * @param url Request URL
     * @param headers Request headers
     * @return Response message
     * @throws IOException
     */

    @JvmStatic
    @Throws(IOException::class)
    fun sendGet(url: String, headers: Map<String, String>): String {
        val requestBuilder = Request.Builder().url(url)
        for ((key, value) in headers) {
            requestBuilder.addHeader(key, value)
        }
        val request = requestBuilder.build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body.string()
        }
    }
}