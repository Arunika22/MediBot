package com.example.myapplication.activities.home.ui.appointment

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.R
import com.example.myapplication.activities.BaseActivity
import com.example.myapplication.activities.home.MainActivity
import com.example.myapplication.databinding.ActivityMakeAppointmentBinding
import com.example.myapplication.utils.SharedPreferencesUtils
import com.google.android.material.chip.Chip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MakeAppointment : BaseActivity() {

    private lateinit var binding: ActivityMakeAppointmentBinding
    private var selectedDate: String = "14" // Default selected date
    private var selectedTime: String = "12:00 PM"

    // OkHttp client and constants
    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaType()
    // Replace with your actual auth token
    private lateinit var AUTH_TOKEN:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakeAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get patient details
        val sharedPreferencesUtils = SharedPreferencesUtils(this)
        val userName=sharedPreferencesUtils.getString("userName")
        AUTH_TOKEN=sharedPreferencesUtils.getString("token")
        binding.etFullName.setText(userName)

        setupAgeSpinner()
        setupChipListeners()
        setupSetAppointmentButton()
        setupBackButton()
        setupDateChips()
    }

    private fun setupAgeSpinner() {
        val ageRanges = arrayOf("18 - 25 yrs", "26 - 30 yrs", "31 - 40 yrs", "41 - 50 yrs", "51 - 60 yrs", "60+ yrs")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ageRanges)
        binding.spAge.adapter = adapter
        binding.spAge.setSelection(1) // Default to "26 - 30" as shown in the image
    }

    private fun setupChipListeners() {
        // Date chips listener
        binding.dateChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chip = findViewById<Chip>(checkedIds[0])
                // Extract just the date number from "14\nTUE" format
                selectedDate = chip.text.toString().split("\n")[0]
                Toast.makeText(this, "Selected date: $selectedDate", Toast.LENGTH_SHORT).show()
            }
        }

        // Time chips listeners - we need to set up listeners for each row
        setupTimeChipListeners()
    }

    private fun setupTimeChipListeners() {
        // Get all time chips
        val timeChips = listOf(
            binding.chipTime1, binding.chipTime2, binding.chipTime3,
            binding.chipTime4, binding.chipTime5, binding.chipTime6,
            binding.chipTime7, binding.chipTime8, binding.chipTime9
        )

        // Set listeners for each chip
        timeChips.forEach { chip ->
            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    selectedTime = buttonView.text.toString()
                    Toast.makeText(this, "Selected time: $selectedTime", Toast.LENGTH_SHORT).show()

                    // Ensure other chips in different rows are unchecked
                    timeChips.forEach { otherChip ->
                        if (otherChip != chip && otherChip.isChecked) {
                            otherChip.isChecked = false
                        }
                    }
                }
            }
        }
    }

    private fun setupSetAppointmentButton() {
        binding.btnSetAppointment.setOnClickListener {
            showLoading("Making appointment...")
            val fullName = binding.etFullName.text.toString()
            val ageRange = binding.spAge.selectedItem.toString()
            val gender = if (binding.rbMale.isChecked) "Male" else "Female"

            // Validate input
            if (fullName.isBlank()) {
                Toast.makeText(this, "Please enter your full name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Convert time format to 24-hour format
            val timeSlot = convertTimeToApiFormat(selectedTime)

            // Format the date correctly - this assumes you have the complete date information
            // in your chip text or you need to build the complete date
            val formattedDate = formatDateForApi(selectedDate)

            // Hard-coded doctor name for this example, in a real app you might get this from elsewhere
            val doctorName = intent.getStringExtra("DOCTOR_NAME")

            // Call API to book appointment
            if (doctorName != null) {
                bookAppointment(doctorName, fullName, timeSlot, formattedDate)
            }else{
                Toast.makeText(this, "Doctor name not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Convert time from "12:30 PM" format to "12:30-13:30" format
    private fun convertTimeToApiFormat(selectedTime: String): String {
        try {
            val inputFormat = SimpleDateFormat("h:mm a", Locale.US)
            val outputFormat = SimpleDateFormat("HH:mm", Locale.US)

            val time = inputFormat.parse(selectedTime)
            val startTime = outputFormat.format(time)

            // Add 1 hour to get end time
            val calendar = Calendar.getInstance()
            calendar.time = time
            calendar.add(Calendar.HOUR_OF_DAY, 1)
            val endTime = outputFormat.format(calendar.time)

            return "$startTime-$endTime"
        } catch (e: Exception) {
            Log.e(TAG, "Error converting time format", e)
            return "00:00-00:00" // Fallback
        }
    }

    // Update the formatDateForApi function:
    private fun formatDateForApi(dateText: String): String {
        try {
            // Parse the date text like "THU 10 APR"
            val parts = dateText.split(" ")
            if (parts.size >= 3) {
                val dayOfMonth = parts[1].toInt() // Extract the day number (10)
                val monthStr = parts[2] // Get the month abbreviation (APR)

                val calendar = Calendar.getInstance()
                // Set the day of month
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Set the month based on abbreviation
                when (monthStr.uppercase()) {
                    "JAN" -> calendar.set(Calendar.MONTH, Calendar.JANUARY)
                    "FEB" -> calendar.set(Calendar.MONTH, Calendar.FEBRUARY)
                    "MAR" -> calendar.set(Calendar.MONTH, Calendar.MARCH)
                    "APR" -> calendar.set(Calendar.MONTH, Calendar.APRIL)
                    "MAY" -> calendar.set(Calendar.MONTH, Calendar.MAY)
                    "JUN" -> calendar.set(Calendar.MONTH, Calendar.JUNE)
                    "JUL" -> calendar.set(Calendar.MONTH, Calendar.JULY)
                    "AUG" -> calendar.set(Calendar.MONTH, Calendar.AUGUST)
                    "SEP" -> calendar.set(Calendar.MONTH, Calendar.SEPTEMBER)
                    "OCT" -> calendar.set(Calendar.MONTH, Calendar.OCTOBER)
                    "NOV" -> calendar.set(Calendar.MONTH, Calendar.NOVEMBER)
                    "DEC" -> calendar.set(Calendar.MONTH, Calendar.DECEMBER)
                }

                // Format the date as YYYY-MM-DD
                val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                return apiFormat.format(calendar.time)
            } else {
                Log.e(TAG, "Invalid date format: $dateText")
                // Fallback to current date
                val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                return apiFormat.format(Calendar.getInstance().time)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing date: $dateText", e)
            // Fallback to current date
            val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return apiFormat.format(Calendar.getInstance().time)
        }
    }

    private fun setupBackButton() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setupDateChips() {
        val calendar = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE d MMM", Locale.getDefault())
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val chips = listOf(
            binding.chipDate1,
            binding.chipDate2,
            binding.chipDate3,
            binding.chipDate4
        )

        // Set today's date and next 3 days
        chips.forEachIndexed { index, chip ->
            // Get date for this chip
            if (index > 0) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            val dateText = dayFormat.format(calendar.time).uppercase()
            chip.text = dateText
        }
    }

    private fun bookAppointment(doctorName: String, patientName: String, timeSlot: String, date: String) {
        // Create JSON request body
        val jsonObject = JSONObject().apply {
            put("doctorName", doctorName)
            put("patientName", patientName.trim())
            put("timeSlot", timeSlot)
            put("date", date)
        }

        val requestBody = jsonObject.toString().toRequestBody(JSON)

        // Log the request body for debugging
        Log.d("payload_makeAppointment", "Request Body: ${jsonObject.toString()}")

        // Create request with headers
        val request = Request.Builder()
            .url("https://medibot-8u6y.onrender.com/v1/api/appointment")
            .post(requestBody)
            .header("Authorization", "Bearer $AUTH_TOKEN")
            .header("Content-Type", "application/json")
            .build()

        // Use coroutines to make network call off the main thread
        CoroutineScope(Dispatchers.IO).launch {
            try {
                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()

                    withContext(Dispatchers.Main) {
                        hideLoading()
                        if (response.isSuccessful && responseBody != null) {
                            handleSuccessResponse(responseBody)
                        } else {
                            handleErrorResponse(responseBody)
                        }
                    }
                }
            } catch (e: IOException) {
                hideLoading()
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "Network error", e)
                    Toast.makeText(this@MakeAppointment, "Network error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun handleSuccessResponse(responseBody: String) {
        try {
            val jsonResponse = JSONObject(responseBody)
            val message = jsonResponse.getString("message")
            if (jsonResponse.has("appointment")) {
                val appointment = jsonResponse.getJSONObject("appointment")
                val doctorName = appointment.getString("doctorName")
                val patientName = appointment.getString("patientName")
                val timeSlot = appointment.getString("timeSlot")
                val date = appointment.getString("date")
                val id = appointment.getString("_id")

                Log.d(TAG, "Appointment booked successfully with ID: $id")
                Toast.makeText(this, "Appointment booked: $doctorName at $timeSlot", Toast.LENGTH_LONG).show()
                startActivity(Intent(this,MainActivity::class.java))

                // Here you can update UI, navigate to a confirmation screen, etc.
                // For example, finish this activity or navigate to a confirmation screen
                finishAffinity()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing response", e)
            Toast.makeText(this, "Error parsing response: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleErrorResponse(responseBody: String?) {
        try {
            hideLoading()
            if (responseBody != null) {
                val jsonResponse = JSONObject(responseBody)
                val message = jsonResponse.optString("message", "Unknown error")
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Unknown error occurred", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing error response", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}