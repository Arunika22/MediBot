package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.TranslateAnimation
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.activities.home.MainActivity
import com.example.myapplication.databinding.ActivitySplashBinding
import com.example.myapplication.utils.SharedPreferencesUtils

class SplashScreen : AppCompatActivity() {

    private var binding:ActivitySplashBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val slideAnimation = TranslateAnimation(-100f, 0f, 0f, 0f)
        slideAnimation.duration = 1200
        binding?.tvTitle?.startAnimation(slideAnimation)

        Handler().postDelayed({
            val sharedPreferencesUtils=SharedPreferencesUtils(this)
            if (sharedPreferencesUtils.getString("token").isNotEmpty()) {
                // User is already logged in, navigate to MainActivity
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(androidx.appcompat.R.anim.abc_slide_in_bottom,1)
            } else {
                startActivity(Intent(this, WalkthroughActivity::class.java))
                overridePendingTransition(androidx.appcompat.R.anim.abc_slide_in_bottom,1)
            }
            finish()//finishes this activity
        },1700)
    }
}
