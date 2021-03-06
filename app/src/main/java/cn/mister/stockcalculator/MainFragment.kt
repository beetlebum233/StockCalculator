package cn.mister.stockcalculator

import android.animation.ObjectAnimator
import android.app.Fragment
import android.app.SearchManager
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.mister.stockcalculator.databinding.FragmentMainBinding
import kotlinx.android.synthetic.main.fragment_main.*
import android.database.MatrixCursor
import android.databinding.ObservableField
import android.provider.BaseColumns
import android.graphics.Color
import android.view.MotionEvent
import android.widget.*
import cn.mister.stockcalculator.event.SuggestionChangeEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import android.widget.TextView
import cn.mister.stockcalculator.calculator.CalculatorNavigator
import cn.mister.stockcalculator.viewmodel.CalculatorViewModel
import cn.mister.stockcalculator.entity.StockForm
import cn.mister.stockcalculator.entity.StockQuote
import cn.mister.stockcalculator.entity.TradingList
import java.lang.ref.WeakReference
import android.animation.AnimatorSet
import android.content.Intent
import android.graphics.Rect


class MainFragment : Fragment(), CalculatorNavigator {


    var translationY = 0

    private var calculator: CalculatorViewModel? = null

    private var suggestionAdapter: CursorAdapter? = null
    private var suggestions = ArrayList<StockQuote>()

    // MARK: life cycle methods

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentMainBinding = DataBindingUtil.inflate(inflater!!, R.layout.fragment_main, container, false)
        calculator = CalculatorViewModel(WeakReference(this))
        binding.calculator = calculator

        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val searchManager = activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        search_view.setSearchableInfo(searchManager.getSearchableInfo(activity.componentName))
        search_view.setIconifiedByDefault(false) // Do not iconify the widget; expand it by default
        suggestionAdapter = SimpleCursorAdapter(activity,
                android.R.layout.simple_list_item_1,
                null,
                arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1),
                intArrayOf(android.R.id.text1),
                0)

        search_view.suggestionsAdapter = suggestionAdapter
        search_view.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {
                search_view.setQuery(suggestions[position].name, false)
//                search_view.clearFocus()
//                doSearch(suggestions.get(position))
                calculator!!.search(suggestions[position].code!!)
                showTradingList(true)
                return true
            }
        })

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if(newText == ""){
                    return true
                }
                ScrapingUtil.getStocksSuggestion(newText, { newSuggestions ->
                    EventBus.getDefault().post(SuggestionChangeEvent(newSuggestions))
                })
                return false
            }
        })

        val id = search_view.context.resources.getIdentifier("android:id/search_src_text", null, null)
        val textView = search_view.findViewById(id) as TextView
        textView.setTextColor(Color.WHITE)

        buy_in_et.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                try{
                    val value = buy_in_et.text.toString().toDouble()
                    buy_in_et.setText(String.format("%.2f", value))
                }catch (e: Exception){

                }

            }
        }
        sell_out_et.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                try{
                    val value = sell_out_et.text.toString().toDouble()
                    sell_out_et.setText(String.format("%.2f", value))
                }catch (e: Exception){

                }
            }
        }
        commission_ratio_et.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                try {
                    val value = commission_ratio_et.text.toString().toDouble()
                    commission_ratio_et.setText(String.format("%.1f", value))
                } catch (e: Exception) {

                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    // subscribe methods

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changeSuggestions(event: SuggestionChangeEvent){
        val newSuggestions = event.stocks
        suggestions.clear()
        suggestions.addAll(newSuggestions)
        val columns = arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_INTENT_DATA)

        val cursor = MatrixCursor(columns)

        for (i in 0 until newSuggestions.size) {
            val tmp = arrayOf(i, newSuggestions[i].name, newSuggestions[i])
            cursor.addRow(tmp)
        }
        suggestionAdapter?.swapCursor(cursor)
    }

    override fun showTradingList(isShown: Boolean) {

        var y = 0
        if(isShown){
            y = translationY
        }else{
            translationY = trading_list_tv.top - stock_form.top - stock_form.height - 20
            y = -translationY
        }

        val animation1 = ObjectAnimator.ofFloat(trading_list, "translationY", y.toFloat())
        val animation2 = ObjectAnimator.ofFloat(trading_list_tv, "translationY", y.toFloat())
        val animatorSet = AnimatorSet()
        animation1.duration = 500
        animation2.duration = 500
        animatorSet.playTogether(animation1, animation2)
        animatorSet.start()
    }

    override fun showExplain() {
        val intent = Intent(activity, ExplainActivity::class.java)
        startActivity(intent)
    }

}