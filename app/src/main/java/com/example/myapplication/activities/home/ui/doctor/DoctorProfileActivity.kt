package com.example.myapplication.activities.home.ui.doctor

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityDoctorProfileBinding

class DoctorProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        // Load doctor image
        Glide.with(this)
            .load("https://imgs.search.brave.com/Tw_Wh1rl8hVTb9XRQ7rLFYstwaRA10ZhizRuDztxPPg/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly90My5m/dGNkbi5uZXQvanBn/LzAyLzk1LzUxLzgw/LzM2MF9GXzI5NTUx/ODA1Ml9hTzVkOUNx/UmhQbmpsTkRUUkRq/S0xaSE5mdHFmc3h6/SS5qcGc") // Replace with your actual image URL or resource
            .centerCrop()
            .into(binding.imgDoctor)

        // Set doctor data
        with(binding) {
            tvDoctorName.text = "Dr. BR Khandelwaal"
            tvSpecialty.text = "Heart"
            tvHospital.text = "Clinora Hospital"
            tvDescription.text = "Dr. Khandelwaal is one of the best doctors in the Persahabatan Hospital. He has saved more than 1000 patients in the past 3 years. He has also received many awards from domestic and abroad as the best doctors. He is available on a private or schedule."
            tvExperience.text = "3"
            tvPatients.text = "1221"
            tvRating.text = "5.0"
        }
    }

    private fun setupListeners() {
        // Back button click listener
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Bookmark button click listener
        binding.btnBookmark.setOnClickListener {
            if (binding.btnBookmark.isChecked){
                Toast.makeText(this, "Added to bookmarks", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Removed from bookmarks", Toast.LENGTH_SHORT).show()
            }

        }

        // Chat button click listener
        binding.cardChat.setOnClickListener {
            Toast.makeText(this, "Opening chat", Toast.LENGTH_SHORT).show()
        }

        // Appointment button click listener
        binding.cardAppointment.setOnClickListener {
            Toast.makeText(this, "Making appointment", Toast.LENGTH_SHORT).show()
        }
    }
}