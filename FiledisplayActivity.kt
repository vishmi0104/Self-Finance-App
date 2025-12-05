package com.example.moneymap

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymap.adapter.TransactionAdapter
import com.example.moneymap.model.Transaction
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

class FiledisplayActivity : AppCompatActivity() {

    private lateinit var filePathTextView: TextView
    private lateinit var transactionRecyclerView: RecyclerView
    private lateinit var transactionList: MutableList<Transaction>
    private lateinit var transactionAdapter: TransactionAdapter
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filedisplay)

        filePathTextView = findViewById(R.id.filePathTextView)
        transactionRecyclerView = findViewById(R.id.transactionRecyclerView)
        transactionRecyclerView.layoutManager = LinearLayoutManager(this)

        val filePath = intent.getStringExtra("filePath")
        filePathTextView.text = "File Path: $filePath"

      //  transactionList = mutableListOf()
      //  transactionAdapter = TransactionAdapter(transactionList)
        transactionRecyclerView.adapter = transactionAdapter

        filePath?.let { readFileData(it) }
    }

    private fun readFileData(filePath: String) {
        try {
            val file = File(filePath)
            if (!file.exists()) {
                filePathTextView.text = "Error: File does not exist at $filePath"
                Log.e("FileDisplay", "File does not exist at $filePath")
                return
            }

            val inputStream = file.inputStream()
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = reader.readLine()
            var lineNumber = 0

            while (line != null) {
                lineNumber++
                Log.d("FileDisplay", "Reading line $lineNumber: $line")
                val parts = line.split(",")
                Log.d("FileDisplay", "Parts: ${parts.joinToString()}")

                if (parts.size == 5) {
                    val title = parts[1].trim()
                    val amountStr = parts[2].trim()
                    val category = parts[3].trim()
                    val dateStr = parts[4].trim()

                    if (title.isNotBlank() && amountStr.isNotBlank() && category.isNotBlank() && dateStr.isNotBlank()) {
                        val amount = amountStr.toDoubleOrNull()
                        val date = dateFormat.parse(dateStr)

                        if (amount != null && date != null) {
                            val transaction = Transaction(
                                title = title,
                                amount = amount,
                                category = category,
                                date = date
                            )
                            Log.d("FileDisplay", "Created transaction: $transaction")
                            transactionList.add(transaction)
                        } else {
                            Log.e("FileDisplay", "Invalid data on line $lineNumber: amount=$amountStr, date=$dateStr")
                        }
                    } else {
                        Log.e("FileDisplay", "Incomplete data on line $lineNumber: $line")
                    }
                } else {
                    Log.w("FileDisplay", "Skipping line $lineNumber with incorrect number of parts: ${parts.size}")
                }

                line = reader.readLine()
            }
            reader.close()
            if (isFinishing) return
            transactionAdapter.submitList(transactionList.toList())
        } catch (e: Exception) {
            val errorMessage = "Error reading file: ${e.message}"
            filePathTextView.text = errorMessage
            Log.e("FileDisplay", errorMessage, e)
        }
    }
}

