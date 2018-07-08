package cn.mister.stockcalculator

import android.util.Log
import cn.mister.stockcalculator.entity.StockQuote
import cn.mister.stockcalculator.entity.StockSuggestion
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder

object ScrapingUtil {

    private val client = OkHttpClient()
    private const val urlPrefix = "https://xueqiu.com/S/"
    private var header : String? = null
    fun getResponse(stockCode: String): String? {
        val request = Request.Builder()
                .url(urlPrefix + stockCode)
                .build()

        val response = client.newCall(request).execute()
        return response.body()?.string()
    }

    fun getQuote(stockCode: String, callback: (stock: StockQuote) -> Unit) {
        Thread {
            val rawStr = getResponse(stockCode)
            if (rawStr != null) {
                val regex = Regex("quote:.*\\}")
                val result = regex.find(rawStr)
                if (result != null) {
                    var quoteStr = result.groupValues[0]
                    quoteStr = quoteStr.substring(quoteStr.indexOf("{"))
                    Log.i("stock", quoteStr)
                    val gson = Gson()
                    val stock = gson.fromJson(quoteStr, StockQuote::class.java)
                    Log.i("stock", stock.toString())
                    callback(stock)
                }
            }
        }.start()
    }

    fun getStocksSuggestion(keyword: String, callback: (suggestions: List<StockQuote>) -> Unit) {
        Thread {
            if(header == null){
                getCookie()
            }
            val keywordParam = URLEncoder.encode(keyword)
            val request = Request.Builder()
                    .url("https://xueqiu.com/stock/search.json?size=3&code=$keywordParam")
                    .header("Cookie", header!!)
                    .build()
            val response = client.newCall(request).execute()
            val rawStr = response.body()?.string()
            if (rawStr != null) {
                println(rawStr)
                val gson = Gson()
                val suggestion: StockSuggestion? = gson.fromJson(rawStr, StockSuggestion::class.java)
                if (suggestion?.stocks != null) {
                    callback(suggestion.stocks!!)
                } else {
                    getCookie()
                }
            }
        }.start()
    }

    fun getCookie(){
        val request = Request.Builder()
                .url("https://xueqiu.com")
                .build()
        val response = client.newCall(request).execute()
        val headers = response.headers("Set-Cookie")
        header = ""
        for (headerStr in headers) {
            val header = getHeader(headerStr)
            if (header != null){
                ScrapingUtil.header += getHeader(header)
            }
        }
    }

    private fun getHeader(headerStr: String): String? {
        val regex = Regex("\\S*=\\S*;")
        val result = regex.find(headerStr)
        if (result != null) {
            return result.groupValues[0]
        }else{
            return null
        }
    }
}