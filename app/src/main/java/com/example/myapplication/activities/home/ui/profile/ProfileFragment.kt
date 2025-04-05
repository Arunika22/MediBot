package com.example.myapplication.activities.home.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myapplication.R

class ProfileFragment : Fragment() {

    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var logoutBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_user_profile, container, false)

        // Bind views
        nameTextView = view.findViewById(R.id.userName)
        emailTextView = view.findViewById(R.id.userEmail)
        logoutBtn = view.findViewById(R.id.logoutBtn)

        // Simulate getting email from activity or shared preferences
        val email = arguments?.getString("email") ?: "user@example.com"

        nameTextView.text = generateNameFromEmail(email)
        emailTextView.text = email

        logoutBtn.setOnClickListener {
            // Log out logic or navigation
            requireActivity().finish() // or navigate back to login
        }

        return view
    }

    private fun generateNameFromEmail(email: String): String {
        val namePart = email.substringBefore("@").replace(".", " ")
        return namePart.split(" ").joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }
    }
}
