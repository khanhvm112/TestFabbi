package com.example.testfabbi.adapter.step

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.testfabbi.R
import com.example.testfabbi.databinding.ItemStepBinding
import com.example.testfabbi.models.Step

class StepViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    private val binding: ItemStepBinding? = DataBindingUtil.bind(v)
    fun onBind(context: Context, item : Step) {
        binding?.let {
            binding.number.text = item.number.toString()
            binding.title.text = item.title

            if (item.isSelected) {
                binding.number.setBackgroundResource(R.drawable.background_step_selected)
                binding.number.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                binding.number.setBackgroundResource(R.drawable.background_step_unselected)
                binding.number.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
        }
    }
}