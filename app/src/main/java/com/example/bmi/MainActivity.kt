package com.example.bmi

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmi.databinding.ActivityMainBinding
import com.example.bmi.databinding.ItemBmiHistoryBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    companion object {
        val BMI_RESULT: String = "bmiResult"
        val BMI_COLOR: String = "bmiColor"
        val UNITS_TYPE: String = "unitsType"
        val HISTORY_LIST: String = "historyList"
        val HISTORY_SHOWING: String = "historyShowing"
    }

    private lateinit var binding: ActivityMainBinding
    private var bmiCalc: BMICalc = BMICalc(BMICalc.UnitType.KgAndCm)

    private lateinit var historyViewManager: RecyclerView.LayoutManager
    private lateinit var historyViewAdapter: RecyclerView.Adapter<*>
    private lateinit var historyData: MutableList<HistoryItemData>
    private var historyShowingBefore = false

    private lateinit var dbHelper: DBHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarT)

        dbHelper = DBHelper(this)
        historyData = dbHelper.getHistory()
        historyViewManager = LinearLayoutManager(this)
        historyViewAdapter = HistoryAdapter(historyData, this)
        binding.historyRV.addItemDecoration(
            DividerItemDecoration(
                binding.historyRV.context,
                DividerItemDecoration.VERTICAL
            )
        )

        binding.historyRV.apply {
            layoutManager = historyViewManager
            adapter = historyViewAdapter
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //TODO oprogramowac zapamietywanie stanu ui (tam gdzie potrzeba)
        outState.run {
            putString(BMI_RESULT, binding.bmiTV.text.toString())
            putInt(BMI_COLOR, binding.bmiTV.currentTextColor)
            putInt(UNITS_TYPE, bmiCalc.inputType.ordinal)
            val isVisible = binding.historyRV.isVisible
            putBoolean(HISTORY_SHOWING, isVisible)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.apply {
            savedInstanceState.run {
                bmiTV.text = getString(BMI_RESULT)
                bmiTV.setTextColor(getInt(BMI_COLOR))
                if (bmiCalc.inputType.ordinal != getInt(UNITS_TYPE)) {
                    changeUnits()
                }
                historyShowingBefore = getBoolean(HISTORY_SHOWING)
                if (historyShowingBefore) {
                    showHistory(null)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bmi_calc_toolbar, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (bmiCalc.inputType == BMICalc.UnitType.KgAndCm) {
            menu.findItem(R.id.units_change).title = "Change to lb/in"
        } else {
            menu.findItem(R.id.units_change).title = "Change to kg/cm"
        }
        if (historyShowingBefore) {
            menu.findItem(R.id.show_history).title = "Close history"
            historyShowingBefore = false
        }
        return super.onPrepareOptionsMenu(menu)
    }

    fun changeUnits() {
        binding.apply {
            if (bmiCalc.inputType == BMICalc.UnitType.KgAndCm) {
                bmiCalc = BMICalc(BMICalc.UnitType.LbAndIn)
                heightTV.text = resources.getString(R.string.height_in)
                massTV.text = resources.getString(R.string.mass_lb)
            } else {
                bmiCalc = BMICalc(BMICalc.UnitType.KgAndCm)
                heightTV.text = resources.getString(R.string.height_cm)
                massTV.text = resources.getString(R.string.mass_kg)
                //item?.title = "Change to lb/in"
            }
        }
    }

    fun showHistory(item: MenuItem?) {
        if (historyData.isEmpty()) {
            Toast.makeText(this, "No History saved", Toast.LENGTH_SHORT).show()
            return
        }

        if (item != null) Log.i("show", item.toString())
        else Log.i("show", "itemNULL")
        historyViewAdapter.notifyDataSetChanged()
        binding.apply {
            if (historyRV.visibility == View.VISIBLE) {
                historyRV.visibility = View.GONE
                item?.title = "Show history"
            } else {
                historyRV.visibility = View.VISIBLE
                item?.title = "Close history"
            }
        }
    }

    fun getBmiColor(bmi: Double): Int {
        return when (bmi) {
            in 0.0..18.5 -> R.color.bmiUnderWeight
            in 18.5..25.0 -> R.color.bmiOptimalWeight
            in 25.0..30.0 -> R.color.bmiOverWeight
            else -> R.color.bmiObese
        }
    }

    fun count(view: View) {
        binding.apply {
            var isError: Boolean = false
            //TODO oprogramowac liczenie bmi i sprawdzanie danych wejsciowych
            if (heightET.text.isBlank()) {
                heightET.error = getString(R.string.height_is_empty)
                isError = true
            } else if (heightET.text.toString().toDouble() <= 0) {
                heightET.error = getString(R.string.invalid_value)
                isError = true
            }
            if (massET.text.isBlank()) {
                massET.error = getString(R.string.mass_is_empty)
                isError = true
            } else if (massET.text.toString().toDouble() < 0) {
                massET.error = getString(R.string.invalid_value)
                isError = true
            }

            if (isError) {
                return
            }


            val bmi =
                bmiCalc.calc(massET.text.toString().toDouble(), heightET.text.toString().toDouble())

            bmiTV.text = String.format("%.2f", bmi)
            bmiTV.setTextColor(resources.getColor(getBmiColor(bmi!!), resources.newTheme()))

            val date: String =
                SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date())

            val historyItem = HistoryItemData(
                heightET.text.toString().toDouble(),
                massET.text.toString().toDouble(),
                bmiCalc.inputType,
                date
            )
            historyData.add(historyItem)
            dbHelper.insertHistoryItem(
                historyItem.mass, historyItem.height, historyItem.unitType.ordinal, historyItem.date
            )
            if (historyData.size > 10) {
                dbHelper.deleteOldestItem()
                historyData.removeAt(0)
            }
            historyViewAdapter.notifyDataSetChanged()
        }
    }

    fun showInfo(view: View) {
        val intent: Intent = Intent(this, BmiInfoActivity::class.java)
        intent.putExtra("bmi", binding.bmiTV.text)
        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("activityResult", requestCode.toString())
    }
}