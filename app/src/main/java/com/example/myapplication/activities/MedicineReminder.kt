package com.example.myapplication.activities


import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMedicineReminderBinding
import java.util.Calendar

class MedicineReminder : AppCompatActivity() {

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
            saveReminder()
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

    private fun saveReminder() {
        val medicineName = binding.etMedicineName.text.toString()
        val dosage = binding.etDosage.text.toString()
        val time = binding.btnSelectTime.text.toString()

        // Get selected days
        val selectedDays = mutableListOf<String>()
        if (binding.chipMon.isChecked) selectedDays.add("Mon")
        if (binding.chipTue.isChecked) selectedDays.add("Tue")
        if (binding.chipWed.isChecked) selectedDays.add("Wed")
        if (binding.chipThu.isChecked) selectedDays.add("Thu")
        if (binding.chipFri.isChecked) selectedDays.add("Fri")
        if (binding.chipSat.isChecked) selectedDays.add("Sat")
        if (binding.chipSun.isChecked) selectedDays.add("Sun")

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
            // Show error for time
            return
        }

        if (selectedDays.isEmpty()) {
            // Show error for days
            return
        }

        // TODO: Save reminder to database or shared preferences

        // Reset form and hide it
        resetForm()
        binding.cardInputForm.visibility = View.GONE
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
}