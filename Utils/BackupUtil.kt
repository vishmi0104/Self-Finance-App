package com.example.moneymap.Utils

import android.content.Context
import android.widget.Toast
import com.example.moneymap.model.Transaction
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.IOException

object BackupUtil {
    private const val BACKUP_FILE_NAME = "transactions_backup.txt"

    fun exportTransactionsToTextFile(context: Context, transactions: List<Transaction>) {
        try {
            val fileOutputStream: FileOutputStream = context.openFileOutput(BACKUP_FILE_NAME, Context.MODE_PRIVATE)
            val outputWriter = OutputStreamWriter(fileOutputStream)
            for (transaction in transactions) {
                outputWriter.write("${transaction.type},${transaction.title},${transaction.amount},${transaction.category},${transaction.date}\n")
            }
            outputWriter.close()
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            android.util.Log.e("BackupUtil", "Error exporting transactions: ${e.message}")
            // Consider showing a Toast to the user about the error
        }
    }

    fun restoreTransactionsFromTextFile(context: Context) {
        // Implement your restore logic here if needed
        android.util.Log.d("BackupUtil", "Restore functionality not yet fully implemented")
        // You would read from context.openFileInput(BACKUP_FILE_NAME) and save to your data source
        Toast.makeText(context, "Restore functionality not yet implemented", Toast.LENGTH_SHORT).show()
    }
}