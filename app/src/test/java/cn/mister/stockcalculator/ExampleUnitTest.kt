package cn.mister.stockcalculator

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testHttp(){
        val response = ScrapingUtil.getResponse("SZ000019")
        ScrapingUtil.getQuote("SZ000019", { stock ->
            print(stock)
        })
    }

    @Test
    fun testHeader(){
        ScrapingUtil.getCookie()
    }
}
