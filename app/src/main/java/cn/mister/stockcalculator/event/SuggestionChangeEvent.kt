package cn.mister.stockcalculator.event

import cn.mister.stockcalculator.entity.StockQuote

class SuggestionChangeEvent(var stocks: List<StockQuote>)