package com.example.myapplication.activities


import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMedicineReminderBinding
import com.example.myapplication.utils.SharedPreferencesUtils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.ArrayList
import java.util.Calendar

class MedicineReminder : BaseActivity() {

    private lateinit var binding: ActivityMedicineReminderBinding
    private var selectedHour = 0
    private var selectedMinute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicineReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // Back button click listener
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSeeAllReminders.setOnClickListener {
            startActivity(Intent(this, AllReminders::class.java))
        }

        // Add record button click listener
        binding.btnAddRecord.setOnClickListener {
            binding.cardInputForm.visibility = View.VISIBLE
        }

        // Time picker button click listener
        binding.btnSelectTime.setOnClickListener {
            showTimePickerDialog()
        }

        // Save button click listener
        binding.btnSave.setOnClickListener {
            // Here you would add the code to save the reminder
            // For now, we'll just hide the form
            val sharedPreferencesUtils=SharedPreferencesUtils(this)
            val token = sharedPreferencesUtils.getString("token")
            addMedicineReminder(token)
        }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minuteOfDay ->
                selectedHour = hourOfDay
                selectedMinute = minuteOfDay
                updateSelectedTimeButton(hourOfDay, minuteOfDay)
            },
            hour,
            minute,
            false // 12-hour format
        )

        timePickerDialog.show()
    }

    private fun updateSelectedTimeButton(hour: Int, minute: Int) {
        val formattedHour = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
        val amPm = if (hour >= 12) "PM" else "AM"
        val formattedMinute = if (minute < 10) "0$minute" else "$minute"

        binding.btnSelectTime.text = "$formattedHour:$formattedMinute $amPm"
    }


    private fun resetForm() {
        binding.etMedicineName.text?.clear()
        binding.etDosage.text?.clear()
        binding.btnSelectTime.text = "Select Time"

        // Clear chip selections
        binding.chipMon.isChecked = false
        binding.chipTue.isChecked = false
        binding.chipWed.isChecked = false
        binding.chipThu.isChecked = false
        binding.chipFri.isChecked = false
        binding.chipSat.isChecked = false
        binding.chipSun.isChecked = false

        // Clear any errors
        binding.tilMedicineName.error = null
        binding.tilDosage.error = null
    }

    fun addMedicineReminder(token: String) {
        val medicineName = binding.etMedicineName.text.toString()
        val dosage = binding.etDosage.text.toString()
        val time = binding.btnSelectTime.text.toString()

        // Validate input
        if (medicineName.isEmpty()) {
            binding.tilMedicineName.error = "Please enter medicine name"
            return
        }

        if (dosage.isEmpty()) {
            binding.tilDosage.error = "Please enter dosage"
            return
        }

        if (time == "Select Time") {
            Toast.makeText(this,"Please select a time", Toast.LENGTH_SHORT).show()
            return
        }

//        make a list of days
       val daysList = ArrayList<String>()
        if (binding.chipMon.isChecked){
            daysList.add("Monday")
        }
        if (binding.chipTue.isChecked){
            daysList.add("Tuesday")
        }
        if (binding.chipWed.isChecked){
            daysList.add("Wednesday")
        }
        if (binding.chipThu.isChecked){
            daysList.add("Thursday")
        }
        if (binding.chipFri.isChecked){
            daysList.add("Friday")
        }
        if (binding.chipSat.isChecked){
            daysList.add("Saturday")
        }
        if (binding.chipSun.isChecked){
            daysList.add("Sunday")
        }

        if (daysList.isEmpty()) {
            // Show error for days
            Toast.makeText(this, "Please select at least one day", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading("Setting up the email reminder...")
        // API URL
        val url = "https://medibot-8u6y.onrender.com/v1/api/medicine/add-reminder"

        val formattedHour = String.format("%02d", selectedHour)
        val formattedMinute = String.format("%02d", selectedMinute)
        val time24HrFormat = "$formattedHour:$formattedMinute"

        // JSON Body
        val jsonBody = JSONObject().apply {
            put("medicineName", binding.etMedicineName.text.toString())
            put("dosage", binding.etDosage.text.toString())
            put("time", time24HrFormat)
            put("days", JSONArray(daysList))
        }

        // Request body
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            jsonBody.toString()
        )

        Log.e("token",token)

        // Build request with Bearer Token
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token") // <-- Bearer Token here
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        // OkHttp Client
        val client = OkHttpClient()

        Log.e("payload reminder", "Payload: ${jsonBody.toString()}")

        // Execute request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    hideLoading()
                    Toast.makeText(
                        this@MedicineReminder,
                        "Failed to set reminder: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                runOnUiThread {
                    hideLoading()
                    if (response.isSuccessful) {
                        Log.e("reminder response", "Success: $responseBody")
                        Toast.makeText(this@MedicineReminder, "Reminder set successfully!", Toast.LENGTH_SHORT).show()
                        resetForm()
                        binding.cardInputForm.visibility = View.GONE
                    } else {
                        Toast.makeText(this@MedicineReminder, "Internal Server Error", Toast.LENGTH_SHORT).show()
                        Log.e("reminder response", "Error: ${response.code} $responseBody")
                    }
                }
            }

        })

    }
}