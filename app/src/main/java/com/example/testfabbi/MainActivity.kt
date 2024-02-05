package com.example.testfabbi

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.testfabbi.adapter.dish.DishAdapter
import com.example.testfabbi.adapter.step.StepAdapter
import com.example.testfabbi.databinding.ActivityMainBinding
import com.example.testfabbi.models.Dishes
import com.example.testfabbi.models.Step
import com.example.testfabbi.models.Step3
import com.example.testfabbi.utils.ReadJSONFromAssets
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var stepAdapter: StepAdapter

    private var dishes = Dishes()
    private var listStep: ArrayList<Step> = ArrayList()
    private var listMedal = arrayOf("---", "breakfast", "lunch", "dinner")
    private var listRestaurant: ArrayList<String> = ArrayList()
    private var listDish: ArrayList<String> = ArrayList()
    private var listStep3: ArrayList<Step3> = ArrayList()

    private var currentStep: Int? = null
    private var medal: String = ""
    private var restaurant: String = ""
    private var numOfPeople: String = ""
    private var totalServings = 0
    private var isFirstTime = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performDataBinding()
        getDataFromJsonFile()
        setUpStepAdapter()
        handleButton()
    }

    private fun performDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    private fun getDataFromJsonFile() {
        val jsonString = ReadJSONFromAssets(this, "dishes.json")
        dishes = Gson().fromJson(jsonString, Dishes::class.java)
    }

    private fun setUpStepAdapter() {
        listStep.clear()
        listStep.apply {
            add(Step(1, "Step 1", false))
            add(Step(2, "Step 2", false))
            add(Step(3, "Step 3", false))
            add(Step(4, "Review", false))
        }

        updateCurrentStep()
        updateOption()
        updateButton()
        stepAdapter = StepAdapter(this, listStep)
        binding.layoutStep.rcvStep.apply {
            adapter = stepAdapter
            layoutManager = GridLayoutManager(this@MainActivity, 4)
        }
        stepAdapter.submitList(listStep)
    }

    private fun handleButton() {
        binding.layoutButton.btnPrevious.setOnClickListener {
            if (currentStep == null) return@setOnClickListener
            updateStepWhenPrevious()
        }

        binding.layoutButton.btnNext.setOnClickListener {
            if (currentStep == null) return@setOnClickListener

            if (currentStep == 1) {
                if (validateStep1()) {
                    updateStepWhenNext()
                }
                return@setOnClickListener
            }

            if (currentStep == 2) {
                if (validateStep2()) {
                    updateStepWhenNext()
                }
                return@setOnClickListener
            }

            if (currentStep == 3) {
                if (validateStep3()) {
                    if (totalServings < numOfPeople.toInt()) {
                        Toast.makeText(
                            this,
                            "The total number of dishes should be greater or equal to the number of people",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        updateStepWhenNext()
                    }
                }
                return@setOnClickListener
            }

            if (currentStep == 4) {
                return@setOnClickListener
            }
        }
    }

    private fun validateStep1(): Boolean {
        if (medal.isBlank() || medal == listMedal[0]) {
            binding.layoutOption.textInputLayoutMedal.error = "This field is required"
            return false
        }

        if (numOfPeople.isBlank()) {
            binding.layoutOption.textInputLayoutEdt.error = "This field is required"
            return false
        }

        if (numOfPeople.toInt() > 10) {
            binding.layoutOption.textInputLayoutEdt.error = "The maximum value of this field is 10"
            return false
        }

        return true
    }

    private fun validateStep2(): Boolean {
        if (restaurant.isBlank() || restaurant == listRestaurant[0]) {
            binding.layoutOption.textInputRestaurant.error = "This field is required"
            return false
        }
        return true
    }

    private fun validateStep3(): Boolean {
        var value = true
        totalServings = 0
        for (i in 0 until listStep3.size) {
            if (listStep3[i].dish == "---" || listStep3[i].dish.isBlank()) {
                val view = binding.layoutOption.lnList.getChildAt(i)
                val textInputLayoutDropdown = view.findViewById<TextInputLayout>(R.id.textInputDish)
                textInputLayoutDropdown.error = "This field is required"
                value = false
            }
            totalServings += listStep3[i].noOfServing.toInt()
        }

        return value
    }

    private fun updateCurrentStep() {
        if (currentStep == null) {
            listStep[0].isSelected = true
            currentStep = listStep[0].number
            return
        }

        listStep.forEach {
            if (it.number == currentStep) it.isSelected = true
        }
    }

    private fun updateOption() {
        if (currentStep == null) return

        if (currentStep == 1) {
            binding.layoutOption.optionStep1.visibility = View.VISIBLE
            binding.layoutOption.optionStep2.visibility = View.GONE
            binding.layoutOption.optionStep3.visibility = View.GONE
            binding.layoutOption.optionStep4.visibility = View.GONE
            setUpDropdownMedal()
            setUpEditTextNumOfPeople()
            return
        }

        if (currentStep == 2) {
            binding.layoutOption.optionStep1.visibility = View.GONE
            binding.layoutOption.optionStep2.visibility = View.VISIBLE
            binding.layoutOption.optionStep3.visibility = View.GONE
            binding.layoutOption.optionStep4.visibility = View.GONE
            setUpDropdownRestaurant()
            return
        }

        if (currentStep == 3) {
            binding.layoutOption.optionStep1.visibility = View.GONE
            binding.layoutOption.optionStep2.visibility = View.GONE
            binding.layoutOption.optionStep3.visibility = View.VISIBLE
            binding.layoutOption.optionStep4.visibility = View.GONE
            setUpLayoutStep3()
            return
        }

        if (currentStep == 4) {
            binding.layoutOption.optionStep1.visibility = View.GONE
            binding.layoutOption.optionStep2.visibility = View.GONE
            binding.layoutOption.optionStep3.visibility = View.GONE
            binding.layoutOption.optionStep4.visibility = View.VISIBLE
            setUpLayoutStep4()
            return
        }
    }

    private fun updateButton() {
        if (currentStep == null) return

        if (currentStep == 1) {
            binding.layoutButton.btnNext.visibility = View.VISIBLE
            binding.layoutButton.btnPrevious.visibility = View.INVISIBLE
            return
        }

        binding.layoutButton.btnNext.visibility = View.VISIBLE
        binding.layoutButton.btnPrevious.visibility = View.VISIBLE
        binding.layoutButton.btnNext.text = getString(R.string.next)
        if (currentStep == 4) {
            binding.layoutButton.btnNext.text = getString(R.string.submit)
        }
        return
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateStepWhenPrevious() {
        currentStep = currentStep!! - 1
        listStep.forEach {
            it.isSelected = false
        }
        updateCurrentStep()
        updateOption()
        updateButton()
        stepAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateStepWhenNext() {
        currentStep = currentStep!! + 1
        listStep.forEach {
            it.isSelected = false
        }
        updateCurrentStep()
        updateOption()
        updateButton()
        stepAdapter.notifyDataSetChanged()
    }

    private fun setUpDropdownMedal() {
        val medalAdapter = ArrayAdapter(this, R.layout.item_dropdown, listMedal)
        medalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.layoutOption.dropdownMedal.setAdapter(medalAdapter)

        if (medal.isBlank()) {
            medal = listMedal[0]
            binding.layoutOption.dropdownMedal.setText(listMedal[0], false)
        } else {
            binding.layoutOption.dropdownMedal.setText(medal, false)
        }

        binding.layoutOption.dropdownMedal.setOnItemClickListener { _, _, position, _ ->
            if (position != 0) binding.layoutOption.textInputLayoutMedal.error = ""
            medal = listMedal[position]
        }
    }

    private fun setUpEditTextNumOfPeople() {
        if (numOfPeople.isBlank()) {
            numOfPeople = "1"
            binding.layoutOption.edtNumOfPeople.setText("1")
        }

        binding.layoutOption.edtNumOfPeople.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                binding.layoutOption.edtNumOfPeople.clearFocus()
                hideKeyboard(binding.layoutOption.edtNumOfPeople)
            }
        }

        binding.layoutOption.edtNumOfPeople.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                numOfPeople = s.toString()
                if (s.toString().isNotBlank() && s.toString().toInt() < 10) {
                    binding.layoutOption.textInputLayoutEdt.error = ""
                }
            }
        })
    }

    private fun setUpDropdownRestaurant() {
        listRestaurant.clear()
        val listNameRestaurant: ArrayList<String> = ArrayList()
        listNameRestaurant.add("---")
        dishes.dishes.forEach {
            if (it.availableMeals.contains(medal)) listNameRestaurant.add(it.restaurant)
        }
        listRestaurant = listNameRestaurant.distinct() as ArrayList<String>

        val restaurantAdapter = ArrayAdapter(this, R.layout.item_dropdown, listRestaurant)
        restaurantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.layoutOption.dropdownRestaurant.setAdapter(restaurantAdapter)

        if (restaurant.isBlank()) {
            restaurant = listRestaurant[0]
            binding.layoutOption.dropdownRestaurant.setText(listRestaurant[0], false)
        } else {
            if (listRestaurant.contains(restaurant)) {
                binding.layoutOption.dropdownRestaurant.setText(restaurant, false)
            } else {
                binding.layoutOption.dropdownRestaurant.setText(listRestaurant[0], false)
            }
        }

        binding.layoutOption.dropdownRestaurant.setOnItemClickListener { _, _, position, _ ->
            if (position != 0) binding.layoutOption.textInputRestaurant.error = ""
            if (restaurant != "---" && restaurant != listRestaurant[position]) isFirstTime = true
            restaurant = listRestaurant[position]
        }
    }

    @SuppressLint("InflateParams")
    private fun setUpLayoutStep3() {
        getListDish()

        if (isFirstTime) {
            listStep3.clear()
            binding.layoutOption.lnList.removeAllViews()
            addView()
            isFirstTime = false
        }

        binding.layoutOption.btnAdd.setOnClickListener {
            addView()
        }
    }

    private fun getListDish() {
        listDish.clear()
        listDish.add("---")
        dishes.dishes.forEach {
            if (it.restaurant == restaurant && it.availableMeals.contains(medal)) {
                listDish.add(it.name)
            }
        }
    }

    private fun addView() {
        val childrenView = layoutInflater.inflate(R.layout.item_step3, null, false)
        val btnDeleteDish = childrenView.findViewById<TextView>(R.id.btnDeleteDish)
        val dropdownDish = childrenView.findViewById<AutoCompleteTextView>(R.id.dropdownDish)
        val edtNoOfServings = childrenView.findViewById<TextInputEditText>(R.id.edtNoOfServings)
        val textInputLayoutEdt =
            childrenView.findViewById<TextInputLayout>(R.id.textInputLayoutNoOfServings)
        val textInputLayoutDropdown = childrenView.findViewById<TextInputLayout>(R.id.textInputDish)

        //dropdown
        val dishAdapter = ArrayAdapter(this, R.layout.item_dropdown, listDish)
        dishAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dropdownDish.setAdapter(dishAdapter)
        dropdownDish.setText(listDish[0], false)
        dropdownDish.setOnItemClickListener { _, _, position, _ ->
            val selectedDish = listDish[position]

            var check = true
            var indexExistDish: Int? = null

            for (i in 0 until listStep3.size) {
                if (listStep3[i].dish == selectedDish) {
                    indexExistDish = i
                    check = false
                }
            }

            if (check) {
                listStep3[binding.layoutOption.lnList.indexOfChild(childrenView)].dish =
                    selectedDish
                if (selectedDish != listDish[0]) {
                    textInputLayoutDropdown.error = ""
                }
            } else {
                if (indexExistDish != null) {
                    val view = binding.layoutOption.lnList.getChildAt(indexExistDish)
                    val noOfServings = view.findViewById<TextInputEditText>(R.id.edtNoOfServings)

                    val quantity = listStep3[indexExistDish].noOfServing.toInt() + edtNoOfServings.text.toString().toInt()
                    noOfServings.setText(quantity.toString())
                    listStep3[indexExistDish].noOfServing = quantity.toString()

                    val indexInView = binding.layoutOption.lnList.indexOfChild(childrenView)
                    binding.layoutOption.lnList.removeView(childrenView)
                    listStep3.removeAt(indexInView)
                }
            }
        }

        //edt
        edtNoOfServings.setText("1")
        edtNoOfServings.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                listStep3[binding.layoutOption.lnList.indexOfChild(childrenView)].noOfServing =
                    s.toString()
                if (s.toString().isNotBlank()) {
                    textInputLayoutEdt.error = ""
                } else {
                    textInputLayoutEdt.error = "This field is required"
                }
            }
        })
        edtNoOfServings.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                edtNoOfServings.clearFocus()
                hideKeyboard(edtNoOfServings)
            }
        }

        if (binding.layoutOption.lnList.childCount == 0) {
            btnDeleteDish.visibility = View.GONE
        } else {
            btnDeleteDish.visibility = View.VISIBLE
        }

        btnDeleteDish.setOnClickListener {
            binding.layoutOption.lnList.removeView(childrenView)
        }

        binding.layoutOption.lnList.addView(childrenView)
        listStep3.add(Step3(dropdownDish.text.toString(), edtNoOfServings.text.toString()))
    }

    private fun setUpLayoutStep4() {
        binding.layoutOption.txtMedal.text = medal
        binding.layoutOption.txtNoOfPeople.text = numOfPeople
        binding.layoutOption.txtRestaurant.text = restaurant
        setUpDishAdapter()
    }

    private fun setUpDishAdapter() {
        val dishAdapter = DishAdapter(listStep3)
        binding.layoutOption.rcvDish.adapter = dishAdapter
        dishAdapter.submitList(listStep3)
    }

    private fun hideKeyboard(view: TextInputEditText) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}