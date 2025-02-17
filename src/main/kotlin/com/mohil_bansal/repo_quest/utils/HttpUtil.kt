
package com.mohil_bansal.repo_quest.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

object HttpUtils {

    private val client = OkHttpClient()

    @JvmStatic
    @Throws(IOException::class)
    fun sendGet(url: String): String {
//        return sendGet(url, emptyMap())
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body?.string() ?: ""
        }
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