package com.example.testfabbi.adapter.dish

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListAdapter
import com.example.testfabbi.R
import com.example.testfabbi.models.Step3
import java.util.concurrent.Executors

class DishAdapter(
    val listItem: ArrayList<Step3>
) : ListAdapter<Step3, DishViewHolder>(
    AsyncDifferConfig.Builder(DishDiffUtils())
        .setBackgroundThreadExecutor { Executors.newSingleThreadExecutor() }
        .build()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.item_dish, parent, false)
        return DishViewHolder(view)
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}