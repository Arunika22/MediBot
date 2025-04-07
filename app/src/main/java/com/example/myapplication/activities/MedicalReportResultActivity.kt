package com.example.myapplication.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMedicalReportResultBinding

class MedicalReportResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMedicalReportResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicalReportResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val haemoglobinValue = intent.getDoubleExtra("haemoglobin_value", -1.0)
        val haemoglobinMsg = intent.getStringExtra("haemoglobin_msg") ?: "No message"

        val sugarLevelValue = intent.getDoubleExtra("sugarLevel_value", -1.0)
        val sugarLevelMsg = intent.getStringExtra("sugarLevel_msg") ?: "No message"

        val healthScore = (50..90).random()

        // Display values
        binding.haemoglobinValue.text = "$haemoglobinValue g/dL"
        binding.sugarLevelValue.text = "$sugarLevelValue mg/dL"
        binding.healthScoreValue.text = healthScore.toString()

        // Display messages
        binding.haemoglobinMessage.text = haemoglobinMsg
        binding.sugarLevelMessage.text = sugarLevelMsg
    }

}
