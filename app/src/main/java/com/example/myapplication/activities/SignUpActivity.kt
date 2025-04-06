package com.example.myapplication.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivitySignUpBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException

class SignUpActivity : AppCompatActivity() {

    private var binding: ActivitySignUpBinding? = null
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.btnBack?.setOnClickListener {
            onBackPressed()
        }

        binding?.alreadyHaveAccountLogin?.setOnClickListener {
            onBackPressed()
        }

        binding?.btnLogin?.setOnClickListener {
            if (binding?.checkboxTerms?.isChecked == true) {
                sendSignUpData()
            } else {
                Toast.makeText(this, "Please accept the Terms of Service", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun sendSignUpData() {
        val name = binding?.etName?.text.toString()
        val email = binding?.etEmail?.text.toString()
        val password = binding?.etPassword?.text.toString()

        val json = JSONObject().apply {
            put("name", name)
            put("email", email)
            put("password", password)
        }

        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaType(),
            json.toString()
        )

        val request = Request.Builder()
            .url("https://medibot-8u6y.onrender.com/v1/api/auth/signup")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@SignUpActivity, "Sign-up failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@SignUpActivity, "Sign-up successful!", Toast.LENGTH_SHORT).show()
                        finish() // Go back to login or previous screen
                    } else {
                        Toast.makeText(this@SignUpActivity, "Sign-up failed: ${response.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
