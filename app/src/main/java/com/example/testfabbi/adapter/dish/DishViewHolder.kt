package com.example.testfabbi.adapter.dish

import android.annotation.SuppressLint
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.testfabbi.databinding.ItemDishBinding
import com.example.testfabbi.models.Step3

class DishViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    private val binding: ItemDishBinding? = DataBindingUtil.bind(v)
    @SuppressLint("SetTextI18n")
    fun onBind(item : Step3) {
        binding?.let {
            it.txtDish.text = "${item.dish} - ${item.noOfServing}"
        }
    }
}