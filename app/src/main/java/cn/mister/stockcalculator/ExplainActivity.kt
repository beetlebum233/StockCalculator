package cn.mister.stockcalculator

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_explain.*

class ExplainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explain)
        btn_dismiss.setOnClickListener { _ -> finish() }
    }

}
