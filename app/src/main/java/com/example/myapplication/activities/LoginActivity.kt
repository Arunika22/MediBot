package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.R
import com.example.myapplication.activities.home.MainActivity
import com.example.myapplication.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private var binding: ActivityLoginBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.btnBack?.setOnClickListener {
            onBackPressed()
        }

        binding?.dontHaveAccountSignUp?.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }

        binding?.btnLogin?.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }
}