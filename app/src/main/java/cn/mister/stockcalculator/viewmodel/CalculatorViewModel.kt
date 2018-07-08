package cn.mister.stockcalculator.viewmodel

import android.databinding.ObservableField
import android.view.View
import cn.mister.stockcalculator.ScrapingUtil
import cn.mister.stockcalculator.entity.StockForm
import cn.mister.stockcalculator.entity.StockQuote
import cn.mister.stockcalculator.entity.TradingList
import kotlin.math.cos

class CalculatorViewModel{
    var stock = ObservableField<StockQuote>(StockQuote())
    var stockForm = ObservableField<StockForm>(StockForm())
    var tradingList = ObservableField<TradingList>(TradingList())

    fun calculate(){
        try {
            val buyIn = stockForm.get()?.buyIn?.toDouble()
            val sellOut = stockForm.get()?.sellOut?.toDouble()
            val commissionRatio= stockForm.get()?.commissionRatio?.toDouble()
            val transactionsNumber = stockForm.get()?.transactionsNumber?.toDouble()

            var transferFee = 0.0
            var stampDuty = 0.0
            var buyInCommission = 0.0
            var sellOutCommission = 0.0
            var buyHandlingFee = 0.0
            var sellHandlingFee = 0.0
            var costs = 0.0
            var profit = 0.0

            if(stock.get()?.exchange == "SH"){
                transferFee = transactionsNumber!! * 0.001
            }
            buyInCommission = buyIn!! * transactionsNumber!! * commissionRatio!! / 10000
            sellOutCommission = sellOut!! * transactionsNumber!! * commissionRatio!! / 10000
            stampDuty = sellOut!! * transactionsNumber!! / 1000
            buyHandlingFee = buyInCommission + transferFee
            sellHandlingFee = sellOutCommission + transferFee + stampDuty
            costs =  stampDuty + transferFee + buyHandlingFee + sellHandlingFee
            profit = (sellOut - buyIn) * transactionsNumber - costs
            tradingList.get()?.stampDuty = String.format("%.2f", stampDuty)
            tradingList.get()?.transferFee = String.format("%.2f", transferFee)
            tradingList.get()?.buyHandlingFee = String.format("%.2f", buyHandlingFee)
            tradingList.get()?.sellHandlingFee = String.format("%.2f", sellHandlingFee)
            tradingList.get()?.costs = String.format("%.2f", costs)
            tradingList.get()?.profit = String.format("%.2f", profit)
            tradingList.notifyChange()
        }catch (e: Exception){

        }
    }

    fun search(keyword: String){
        ScrapingUtil.getQuote(keyword, { stock ->
            this.stock.get()?.name = stock.name
            this.stock.get()?.code = stock.code
            this.stock.get()?.current = stock.current
            this.stock.get()?.exchange = stock.exchange
            this.stock.notifyChange()
        })
    }
}