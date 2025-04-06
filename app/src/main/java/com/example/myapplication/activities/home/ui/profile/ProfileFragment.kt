package com.example.myapplication.activities.home.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.activities.LoginActivity
import com.example.myapplication.databinding.FragmentProfileBinding
import com.example.myapplication.utils.SharedPreferencesUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefsUtils: SharedPreferencesUtils

    // List of wellness tips to rotate through
    private val wellnessTips = listOf(
        "💧 Stay hydrated! Aim for 8 glasses of water daily.",
        "🚶 A 30-minute walk can boost your mood and energy.",
        "🥗 Include colorful vegetables in your meals for better nutrition.",
        "😴 Aim for 7-8 hours of quality sleep each night.",
        "🧘 Take 5 minutes to practice mindful breathing when stressed.",
        "🍎 An apple a day keeps the doctor away!",
        "☀️ Get at least 15 minutes of sunlight daily for vitamin D.",
        "📱 Take regular breaks from screen time to rest your eyes."
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize SharedPreferences
        prefsUtils = SharedPreferencesUtils(requireContext())

        // Set up the UI
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        // Load user data from SharedPreferences
        val userName = prefsUtils.getString("userName", "User Name")
        val userEmail = prefsUtils.getString("userEmail", "user@example.com")

        // Set user data to views
        binding.userName.text = userName
        binding.userEmail.text = userEmail

        // Set a random wellness tip
        binding.wellnessTips.text = wellnessTips[Random.nextInt(wellnessTips.size)]

        // Update the last login time
        val lastLogin = prefsUtils.getString("lastLogin", "")
        if (lastLogin.isNotEmpty()) {
            binding.lastLoginInfo.text = "Last login: $lastLogin"
        }


        // Load profile image - here using Glide with a placeholder
        // In a real app, you might load from a URL stored in preferences
        Glide.with(this)
            .load(getRandomUserUrl()) // Replace with actual user image if available
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_user)
            .error(R.drawable.ic_user)
            .into(binding.profilePic)

    }

    private fun setupListeners() {
        // Logout button
        binding.logoutBtn.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        // Refresh wellness tip when clicked
        binding.wellnessTipsCard.setOnClickListener {
            binding.wellnessTips.text = wellnessTips[Random.nextInt(wellnessTips.size)]
            Toast.makeText(requireContext(), "Tip updated!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Logout") { _, _ ->
                logout()
            }
            .show()
    }

    private fun logout() {
        // Clear relevant data from SharedPreferences
        prefsUtils.remove("token")

        // Store last login time before logging out
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val currentTime = dateFormat.format(Date())
        prefsUtils.putString("lastLogin", currentTime)

        // Navigate to Login Activity
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getRandomUserUrl(): String {
        val randomNumber = Random.nextInt(1, 61) // 1 to 60 inclusive
        return "https://randomuser.me/api/portraits/men/$randomNumber.jpg"
    }
}