package cn.mister.stockcalculator.entity

import android.databinding.BaseObservable
import android.databinding.Bindable
import cn.mister.stockcalculator.BR

class StockQuote{
    var name: String? = null
    var code: String? = null
    var current: String? = null
    var exchange: String? = null
}