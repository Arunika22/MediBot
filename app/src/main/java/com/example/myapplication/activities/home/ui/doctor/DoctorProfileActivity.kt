package com.example.myapplication.activities.home.ui.doctor

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.activities.home.ui.appointment.MakeAppointment
import com.example.myapplication.databinding.ActivityDoctorProfileBinding
import com.example.myapplication.models.DoctorData

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
        val doctorName = intent.getStringExtra("DOCTOR_NAME")
        val doctorSpecialty = intent.getStringExtra("DOCTOR_SPECIALIZATION")
        val DoctorImage = intent.getStringExtra("DOCTOR_IMAGE")
        // Load doctor image
        Glide.with(this)
            .load(DoctorImage) // Replace with your actual image URL or resource
            .centerCrop()
            .into(binding.imgDoctor)

        // Set doctor data
        with(binding) {
            tvDoctorName.text = doctorName
            tvSpecialty.text = doctorSpecialty
            tvHospital.text = "Clinora Hospital"
            tvDescription.text = "${doctorName} is one of the best doctors in the Clinora Hospital. He has saved more than 1000 patients in the past 3 years. He has also received many awards from domestic and abroad as the best doctors. He is available on a private or schedule."
            tvExperience.text = (3..6).random().toString()
            tvPatients.text = (1000..2000).random().toString()
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
            startActivity(Intent(this,MakeAppointment::class.java))
        }
    }
}