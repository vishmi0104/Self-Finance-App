package com.example.moneymap

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.util.Calendar
import java.util.Locale

class NotificationActivity : AppCompatActivity() {

    private lateinit var editTextMonthlyBudget: EditText
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var setBudgetButton: Button
    private lateinit var setReminderButton: Button
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>

    // Notification channel ID
    private val CHANNEL_ID = "budget_reminder_channel"
    private val DAILY_REMINDER_CHANNEL_ID = "daily_reminder_channel" //channel for daily reminders

    // Request code for the pending intent - used to distinguish between notifications
    private val BUDGET_NOTIFICATION_REQUEST_CODE = 100
    private val DAILY_REMINDER_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting) // You'll need a new layout file

        // Initialize UI elements
        editTextMonthlyBudget = findViewById(R.id.editTextMonthlyBudget)
        sharedPreferences = getSharedPreferences("budget_settings", MODE_PRIVATE)
        setBudgetButton = findViewById(R.id.saveButtonSettings)
        setReminderButton = findViewById(R.id.setReminderButton)

        // Load saved budget
        val savedBudget = sharedPreferences.getFloat("monthly_budget", 0f)
        editTextMonthlyBudget.setText(if (savedBudget > 0) savedBudget.toString() else "")

        // Initialize the ActivityResultLauncher for permission request
        notificationPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    // Permission granted, set up notifications
                    createNotificationChannels() //call both channels
                    setBudgetButton.setOnClickListener {
                        saveBudgetAndSetNotification()
                    }
                    setReminderButton.setOnClickListener{
                        setDailyReminder()
                    }
                } else {
                    // Permission denied, show a message to the user
                    Toast.makeText(
                        this,
                        "Notification permission is required for budget alerts and reminders.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }


        // Check for notification permission when the activity is created.  moved permission check here
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, set up notifications
            createNotificationChannels() //call both channels.
            setBudgetButton.setOnClickListener {
                saveBudgetAndSetNotification()
            }
            setReminderButton.setOnClickListener{
                setDailyReminder()
            }
        } else {
            // Request permission
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }


    }

    private fun createNotificationChannels() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not available in older API versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //budget notification channel
            val name = "Budget Reminder"
            val descriptionText = "Reminders for budget limits"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            //daily reminder channel
            val dailyName = "Daily Expense Reminder"
            val dailyDescriptionText = "Reminds user to record daily expenses"
            val dailyImportance = NotificationManager.IMPORTANCE_LOW // Importance low for daily
            val dailyChannel = NotificationChannel(DAILY_REMINDER_CHANNEL_ID, dailyName, dailyImportance).apply{
                description = dailyDescriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            notificationManager.createNotificationChannel(dailyChannel) //create daily reminder channel
        }
    }

    private fun saveBudgetAndSetNotification() {
        val budgetString = editTextMonthlyBudget.text.toString().trim()
        val budget = if (budgetString.isNotEmpty()) budgetString.toFloat() else 0f
        sharedPreferences.edit().putFloat("monthly_budget", budget).apply()
        Toast.makeText(this, "Budget Saved", Toast.LENGTH_SHORT).show()

        //set notification
        if(budget > 0){
            setBudgetNotification(budget)
        }

        finish()
    }



    private fun setBudgetNotification(budget: Float) {
        //check for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Notification", "Notification permission not granted")
            return
        }

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 28) // Example: Check on the 28th of each month.  Make this configurable if you want.

        // Intent for the notification
        val intent = Intent(this, NotificationReceiver::class.java).apply {
            action = "BUDGET_EXCEEDED" // Custom action to filter in the receiver
            putExtra("BUDGET", budget)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            BUDGET_NOTIFICATION_REQUEST_CODE, // Use the constant
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
        Log.d("Notification", "Budget notification set for ${calendar.time}")

    }

    private fun setDailyReminder() {
        //check for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Notification", "Notification permission not granted")
            return
        }
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 20) // 8 PM
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        // If it's before 8 PM today, set it for today. Otherwise, set it for tomorrow.
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }


        val intent = Intent(this, NotificationReceiver::class.java).apply {
            action = "DAILY_EXPENSE_REMINDER"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            DAILY_REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating( // Use setRepeating
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,  // Repeat every day
            pendingIntent
        )
        Log.d("Notification", "Daily reminder set for ${calendar.time}")
    }

    class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("NotificationReceiver", "Received notification: ${intent.action}")
            when (intent.action) {
                "BUDGET_EXCEEDED" -> {
                    val budget = intent.getFloatExtra("BUDGET", 0f)
                    // In a real app, you'd fetch the current spending here.  For this example, we'll just use a dummy value
                    val currentSpending = 1200f // Dummy value
                    if (currentSpending > budget) {
                        showBudgetExceededNotification(context, budget, currentSpending)
                    }else{
                        showBudgetApproachingNotification(context, budget, currentSpending)
                    }
                }
                "DAILY_EXPENSE_REMINDER" -> {
                    showDailyReminderNotification(context)
                }
            }


        }

        private fun showBudgetExceededNotification(context: Context, budget: Float, currentSpending: Float) {
            val builder = NotificationCompat.Builder(context, context.getString(R.string.budget_reminder_channel_id))
                .setSmallIcon(R.drawable.notification) // Use a suitable icon
                .setContentTitle("Budget Exceeded")
                .setContentText("Your spending ($$currentSpending) has exceeded your monthly budget of $$budget!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true) // Dismiss on tap

            with(NotificationManagerCompat.from(context)) {
                //Use the constant
               // notify(1, builder.build()) // Use a unique notification ID
            }
        }

        private fun showBudgetApproachingNotification(context: Context, budget: Float, currentSpending: Float) {
            val builder = NotificationCompat.Builder(context, context.getString(R.string.budget_reminder_channel_id))
                .setSmallIcon(R.drawable.notification) // Use a suitable icon
                .setContentTitle("Budget Approaching")
                .setContentText("Your spending is getting close to your monthly budget of $$budget. Current spending: $$currentSpending")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true) // Dismiss on tap

            with(NotificationManagerCompat.from(context)) {
                //notify(2, builder.build()) // Use a unique notification ID
            }
        }

        private fun showDailyReminderNotification(context: Context) {
            val builder = NotificationCompat.Builder(context, context.getString(R.string.daily_reminder_channel_id))
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("Record Your Expenses")
                .setContentText("Don't forget to record your daily expenses!")
                .setPriority(NotificationCompat.PRIORITY_LOW) // Use the correct priority for daily reminders
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                //notify(DAILY_REMINDER_REQUEST_CODE, builder.build())
            }
        }
    }
}
