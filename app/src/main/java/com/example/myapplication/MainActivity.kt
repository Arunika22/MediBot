package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Log.d("MainActivityNew", "Splash Screen started...")

        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("MainActivityNew", "Navigating to Onboarding1Activity")
            startActivity(Intent(this, Onboarding1Activity::class.java))
            finish()
        }, 3000)
    }
}
