package com.example.testfabbi.adapter.step

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListAdapter
import com.example.testfabbi.R
import com.example.testfabbi.models.Step
import java.util.concurrent.Executors

class StepAdapter(
    private val context: Context,
    val listItem: ArrayList<Step>,
) : ListAdapter<Step, StepViewHolder>(
    AsyncDifferConfig.Builder(StepDiffUtils())
        .setBackgroundThreadExecutor { Executors.newSingleThreadExecutor() }
        .build()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.item_step, parent, false)
        return StepViewHolder(view)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        holder.onBind(context, getItem(position))
    }
}