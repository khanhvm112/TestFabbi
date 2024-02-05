package com.example.testfabbi.models

data class Step(
    var number: Int,
    var title: String,
    var isSelected: Boolean = false
)

data class Dishes(
    var dishes: ArrayList<Dish> = ArrayList()
)

data class Dish(
    var id: Int,
    var name: String,
    var restaurant: String,
    var availableMeals: ArrayList<String> = ArrayList()
)

data class Step3(
    var dish: String,
    var noOfServing: String,
)