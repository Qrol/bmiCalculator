package com.example.bmi

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.example.bmi.databinding.ActivityBmiInfoBinding

class BmiInfoActivity : AppCompatActivity() {
    lateinit var binding: ActivityBmiInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setSupportActionBar(binding.myToolbar)

        val bmiStr = intent.getStringExtra("bmi")
        val bmi = bmiStr.toDouble()
        binding.apply {
            bmiTV.text = bmiStr
            personIV.setImageResource(getPerson(bmi))
            descriptionTV.setText(getDescription(bmi))
            bmiContainerCL.setBackgroundColor(resources.getColor(getBmiColor(bmi)))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        //menuInflater.inflate(R.menu., menu)
        return true
    }


    fun getBmiColor(bmi: Double): Int {
        return when (bmi) {
            in 0.0..18.5 -> R.color.bmiUnderWeight
            in 18.5..25.0 -> R.color.bmiOptimalWeight
            in 25.0..30.0 -> R.color.bmiOverWeight
            else -> R.color.bmiObese
        }
    }

    fun getPerson(bmi: Double): Int {
        return when (bmi) {
            in 0.0..18.5 -> R.drawable.underweight_man
            in 18.5..25.0 -> R.drawable.normal_man
            in 25.0..30.0 -> R.drawable.fat_man
            else -> R.drawable.obese_man
        }
    }

    fun getDescription(bmi: Double): Int {
        return when (bmi) {
            in 0.0..18.5 -> R.string.bmi_desc_underweight
            in 18.5..25.0 -> R.string.bmi_desc_optimal
            in 25.0..30.0 -> R.string.bmi_desc_overweight
            else -> R.string.bmi_desc_obese
        }
    }

}