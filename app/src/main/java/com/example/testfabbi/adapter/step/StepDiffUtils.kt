package com.example.testfabbi.adapter.step

import androidx.recyclerview.widget.DiffUtil
import com.example.testfabbi.models.Step

class StepDiffUtils: DiffUtil.ItemCallback<Step>() {
    override fun areItemsTheSame(oldItem: Step, newItem: Step): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Step, newItem: Step): Boolean {
        return oldItem == newItem
    }
}