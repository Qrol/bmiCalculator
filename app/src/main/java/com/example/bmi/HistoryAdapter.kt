package com.example.bmi

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bmi.databinding.ItemBmiHistoryBinding

class HistoryAdapter(
    private val historyDataSet: List<HistoryItemData>,
    private val context: Context
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    inner class HistoryViewHolder(view: ItemBmiHistoryBinding) :
        RecyclerView.ViewHolder(view.root) {
        val binding = ItemBmiHistoryBinding.bind(view.root)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding =
            ItemBmiHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val data: HistoryItemData = historyDataSet[historyDataSet.size - 1 - position]
        holder.binding.apply {
            historyBmiTV.text =
                String.format("%.2f", BMICalc(data.unitType).calc(data.mass, data.height))
            historyMassTV.text = data.mass.toString()
            historyHeightTV.text = data.height.toString()
            historyDateTV.text = data.date
            if (data.unitType.ordinal == BMICalc.UnitType.KgAndCm.ordinal) {
                historyMassLabelTV.text = context.resources.getString(R.string.mass_kg)
                historyHeightLabelTV.text = context.resources.getString(R.string.height_cm)
            } else {
                historyMassLabelTV.text = context.resources.getString(R.string.mass_lb)
                historyHeightLabelTV.text = context.resources.getString(R.string.height_in)
            }
        }
    }

    override fun getItemCount(): Int = historyDataSet.size
}