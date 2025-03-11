package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Onboarding1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding1)

        val nextArrow: ImageView = findViewById(R.id.nextButton)
        nextArrow.setOnClickListener {
            val intent = Intent(this, Onboarding2Activity::class.java)
            startActivity(intent)
            finish() // Close Onboarding1Activity
        }
        val skipTextView: TextView = findViewById(R.id.skipTextView)
        skipTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java) // Change to your main activity
            startActivity(intent)
            finish() // Close Onboarding1Activity
        }
    }
}

