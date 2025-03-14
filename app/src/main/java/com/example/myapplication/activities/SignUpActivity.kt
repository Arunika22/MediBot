package com.example.myapplication.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private var binding:ActivitySignUpBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.btnBack?.setOnClickListener {
            onBackPressed()
        }

        binding?.alreadyHaveAccountLogin?.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }
}