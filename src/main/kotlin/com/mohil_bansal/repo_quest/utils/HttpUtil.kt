package com.mohil_bansal.repo_quest.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.Proxy
import java.net.InetSocketAddress
import kotlin.random.Random
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object HttpUtils {

//    private val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress("185.105.102.189", 8080))
    private val client = OkHttpClient()
    private const val PROXY_LIST_URL = "https://raw.githubusercontent.com/LoneKingCode/free-proxy-db/refs/heads/main/proxies/http.json"


    @JvmStatic
    @Throws(IOException::class)
    fun sendGet(url: String): String {
        val request = Request.Builder()
            .url(url)
            .header(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
            )
            .header("Referer", "https://mvnrepository.com/")
            .header("Accept-Language", "en-US,en;q=0.5")
            .header("Connection", "keep-alive")
            .header("Upgrade-Insecure-Requests", "1")
            .build()

        client.newCall(request).execute().use { response ->
            if (response.code == 403) {
                val proxy = getRandomProxy()
                return sendGetWithProxy(url, proxy)
            }
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body.string()
        }
    }

    @JvmStatic
    @Throws(IOException::class)
    fun sendGetWithProxy(url: String, proxy: Proxy): String {
        val clientWithProxy = OkHttpClient.Builder().proxy(proxy).build()
        val request = Request.Builder()
            .url(url)
            .header(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
            )
            .header("Referer", "https://mvnrepository.com/")
            .header("Accept-Language", "en-US,en;q=0.5")
            .header("Connection", "keep-alive")
            .header("Upgrade-Insecure-Requests", "1")
            .build()

        clientWithProxy.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body.string()
        }
    }

    @JvmStatic
    @Throws(IOException::class)
    fun getRandomProxy(): Proxy {
        val request = Request.Builder().url(PROXY_LIST_URL).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Failed to fetch proxy list")
            val proxyListJson = response.body.string()
            val proxyListType = object : TypeToken<List<ProxyInfo>>() {}.type
            val proxyList: List<ProxyInfo> = Gson().fromJson(proxyListJson, proxyListType)
            val randomProxy = proxyList[Random.nextInt(proxyList.size)]
            return Proxy(Proxy.Type.HTTP, InetSocketAddress(randomProxy.ip, randomProxy.port))
        }
    }

    data class ProxyInfo(
        val ip: String,
        val port: Int
    )

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