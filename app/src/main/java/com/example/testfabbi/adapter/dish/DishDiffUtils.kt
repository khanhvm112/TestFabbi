package com.example.testfabbi.adapter.dish

import androidx.recyclerview.widget.DiffUtil
import com.example.testfabbi.models.Step3

class DishDiffUtils: DiffUtil.ItemCallback<Step3>() {
    override fun areItemsTheSame(oldItem: Step3, newItem: Step3): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Step3, newItem: Step3): Boolean {
        return oldItem == newItem
    }
}