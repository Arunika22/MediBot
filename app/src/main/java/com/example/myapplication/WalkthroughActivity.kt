package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.adapters.OnboardingAdapter
import com.example.myapplication.databinding.ActivityWalkthroughBinding
import com.example.myapplication.models.OnboardingItem

class WalkthroughActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tvTitle: TextView
    private lateinit var indicators: List<View>
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private var binding:ActivityWalkthroughBinding?=null

    private val onboardingItems = listOf(
        OnboardingItem(R.drawable.doctor_image1, "Consult only with a doctor you trust"),
        OnboardingItem(R.drawable.doctor_image2, "Get personalized health tips"),
        OnboardingItem(R.drawable.doctor_image3, "Book your appointment easily")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityWalkthroughBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        viewPager = binding?.viewPager?:ViewPager2(this)
        tvTitle = binding?.tvOnboardingTitle?:TextView(this)

        binding?.btnSkip?.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        setupIndicators()
        setupViewPager()
        startAutoScroll()

        binding?.ButtonAction?.setOnClickListener {
            if (currentPage < onboardingItems.size - 1) {
                currentPage++
            } else {
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }
            viewPager.setCurrentItem(currentPage, true)
        }

    }

    private fun setupIndicators() {
        indicators = listOf(
            findViewById(R.id.indicator1),
            findViewById(R.id.indicator2),
            findViewById(R.id.indicator3)
        )
    }

    private fun setupViewPager() {
        val adapter = OnboardingAdapter(onboardingItems)
        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndicators(position)
                updateTitle(position)
            }
        })
    }

    private fun updateIndicators(position: Int) {
        indicators.forEachIndexed { index, view ->
            view.setBackgroundResource(
                if (index == position) R.drawable.indicator_filled else R.drawable.indicator_unfilled
            )
        }
    }

    private fun updateTitle(position: Int) {
        tvTitle.text = onboardingItems[position].title
    }

    private fun startAutoScroll() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (currentPage < onboardingItems.size - 1) {
                    currentPage++
                } else {
                    currentPage = 0
                }
                viewPager.setCurrentItem(currentPage, true)
                handler.postDelayed(this, 1500) // Change page every 1 second
            }
        }, 1500)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null) // Stop auto-scroll when activity is destroyed
        super.onDestroy()
        binding=null
    }

}

