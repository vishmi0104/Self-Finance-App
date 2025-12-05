package com.example.moneymap

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var radioGroupTransactionType: RadioGroup
    private lateinit var titleEditText: EditText
    private lateinit var amountEditText: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var textViewDate: TextView
    private lateinit var buttonSave: Button
    private lateinit var buttonDelete: Button
    private var transactionId: Long? = null
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var selectedDate: Date = Calendar.getInstance().time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        radioGroupTransactionType = findViewById(R.id.radioGroupTransactionType)
        titleEditText = findViewById(R.id.editTextTitle)
        amountEditText = findViewById(R.id.editTextAmount)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        textViewDate = findViewById(R.id.textViewDate)
        buttonSave = findViewById(R.id.buttonSave)
        buttonDelete = findViewById(R.id.buttonDelete)

        // Populate category spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.transaction_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter
        }

        // Set up date picker
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            selectedDate = calendar.time
            updateDateTextView()
        }

        textViewDate.setOnClickListener {
            DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        updateDateTextView() // Set initial date

        // Load transaction details if editing
        intent.extras?.let {
            transactionId = it.getLong("TRANSACTION_ID")
            val type = it.getString("TRANSACTION_TYPE")
            if (type == "Income") radioGroupTransactionType.check(R.id.radioIncome)
            else radioGroupTransactionType.check(R.id.radioExpense)
            titleEditText.setText(it.getString("TRANSACTION_TITLE"))
            amountEditText.setText(it.getDouble("TRANSACTION_AMOUNT").toString())
            val category = it.getString("TRANSACTION_CATEGORY")
            val categoryPosition =
                (spinnerCategory.adapter as ArrayAdapter<String>).getPosition(category)
            if (categoryPosition != -1) {
                spinnerCategory.setSelection(categoryPosition)
            }
            val dateStr = it.getString("TRANSACTION_DATE")
            selectedDate = dateFormat.parse(dateStr) ?: Calendar.getInstance().time
            updateDateTextView()
            buttonDelete.visibility = View.VISIBLE
        }

        // Save transaction
        buttonSave.setOnClickListener {
            if (validateForm()) {
                val checkedRadioButtonId = radioGroupTransactionType.checkedRadioButtonId
                val type = when (checkedRadioButtonId) {
                    R.id.radioIncome -> "Income"
                    R.id.radioExpense -> "Expense"
                    else -> ""
                }
                val title = titleEditText.text.toString().trim()
                val amountStr = amountEditText.text.toString().trim()
                val category = spinnerCategory.selectedItem.toString()
                val formattedDate = dateFormat.format(selectedDate)

                val amount = amountStr.toDoubleOrNull()
                amount?.let {
                    saveTransaction(type, title, it.toString(), category, formattedDate)
                }
            }
        }

        // Delete transaction
        buttonDelete.setOnClickListener {
            transactionId?.let { deleteTransaction(it) }
        }

        // Back button
        findViewById<ImageButton>(R.id.buttonBack).setOnClickListener {
            finish()
        }
    }

    private fun updateDateTextView() {
        textViewDate.text = dateFormat.format(selectedDate)
    }

    private fun validateForm(): Boolean {
        var isValid = true

        // Validate Transaction Type
        if (radioGroupTransactionType.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Please select transaction type", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // Validate Title
        val title = titleEditText.text.toString().trim()
        if (TextUtils.isEmpty(title)) {
            titleEditText.error = "Title is required"
            isValid = false
        } else if (title.length > 50) {
            titleEditText.error = "Title cannot be more than 50 characters"
            isValid = false
        }

        // Validate Amount
        val amountStr = amountEditText.text.toString().trim()
        if (TextUtils.isEmpty(amountStr)) {
            amountEditText.error = "Amount is required"
            isValid = false
        } else {
            val amount = amountStr.toDoubleOrNull()
            if (amount == null) {
                amountEditText.error = "Invalid amount"
                isValid = false
            } else if (amount <= 0) {
                amountEditText.error = "Amount must be greater than zero"
                isValid = false
            } else if (!Pattern.matches("^\\d+(\\.\\d{1,2})?$", amountStr)) {
                amountEditText.error = "Amount can have up to 2 decimal places"
                isValid = false
            }
        }

        // Validate Category
        if (spinnerCategory.selectedItemPosition == AdapterView.INVALID_POSITION) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun saveTransaction(
        type: String,
        title: String,
        amount: String,
        category: String,
        formattedDate: String
    ) {
        val sharedPreferences = getSharedPreferences("transactions", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val transactionString = "$type,$title,$amount,$category,$formattedDate"

        val key = if (transactionId != null) {
            transactionId.toString()
        } else {
            System.currentTimeMillis().toString()
        }

        editor.putString(key, transactionString)
        editor.apply()
        Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun deleteTransaction(transactionId: Long) {
        val sharedPreferences = getSharedPreferences("transactions", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(transactionId.toString())
        editor.apply()
        Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show()
        finish()
    }
}