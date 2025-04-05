package com.example.myapplication.activities.home.ui.appointment

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMakeAppointmentBinding
import com.google.android.material.chip.Chip

class MakeAppointment : AppCompatActivity() {

    private lateinit var binding: ActivityMakeAppointmentBinding
    private var selectedDate: String = "14" // Default selected date
    private var selectedTime: String = "12:30 PM"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakeAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAgeSpinner()
        setupChipListeners()
        setupSetAppointmentButton()
        setupBackButton()

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
            // Get patient details
            val fullName = binding.etFullName.text.toString()
            val ageRange = binding.spAge.selectedItem.toString()
            val gender = if (binding.rbMale.isChecked) "Male" else "Female"

            // Validate input
            if (fullName.isBlank()) {
                Toast.makeText(this, "Please enter your full name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create appointment summary
            val appointmentSummary = """
                Appointment created successfully!
                
                Patient: $fullName
                Date: July $selectedDate, 2020
                Time: $selectedTime
                Age Range: $ageRange
                Gender: $gender
            """.trimIndent()

            // Show appointment summary
            Toast.makeText(this, "Appointment set successfully!", Toast.LENGTH_LONG).show()

            // In a real app, you would save this information to a database
            // and perhaps navigate to a confirmation screen
        }
    }

    private fun setupBackButton() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }
}