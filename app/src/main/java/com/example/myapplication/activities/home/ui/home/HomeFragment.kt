package com.example.myapplication.activities.home.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medibot.FirstAidActivity
import com.example.myapplication.R
import com.example.myapplication.activities.NotificationsActivity
import com.example.myapplication.adapters.TopDocsAdapter
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.models.DoctorData
import com.example.myapplication.utils.MarginItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.medibot.SymptomCheckerActivity
import com.example.myapplication.activities.MedicalReportAnalysis
import com.example.myapplication.activities.MedicineReminder
import com.example.myapplication.activities.home.MainActivity
import com.example.myapplication.activities.home.ui.doctor.DoctorProfileActivity
import com.example.myapplication.models.DoctorResponse
import com.example.myapplication.utils.SharedPreferencesUtils
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class HomeFragment : Fragment(), TopDocsAdapter.OnDoctorClickListener {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var doctorList: List<DoctorData>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnNotifications.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationsActivity::class.java))
        }

        binding.btnReportAnalysis.setOnClickListener {
            startActivity(Intent(requireContext(),MedicalReportAnalysis::class.java))
        }

        binding.btnDoctor.setOnClickListener {
            // navigate to a fragment using BottomNavigation
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
            // Set the Profile tab as selected
            bottomNav.selectedItemId = R.id.navigation_doctors
        }

        binding.tvSeeAllDocs.setOnClickListener {
            // navigate to a fragment using BottomNavigation
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
            // Set the Profile tab as selected
            bottomNav.selectedItemId = R.id.navigation_doctors
        }

        val sharedPreferencesUtils=SharedPreferencesUtils(requireContext())
        val token = sharedPreferencesUtils.getString("token")
        fetchAllDoctors(token)

        // Set click listener for the symptom checker card
        val symptomCheckerBtn = root.findViewById<CardView>(R.id.btn_symptoms_checker)
        symptomCheckerBtn.setOnClickListener {
            val intent = Intent(requireContext(), SymptomCheckerActivity::class.java)
            startActivity(intent)
        }
        val medicalGuidanceBtn = root.findViewById<CardView>(R.id.btn_medical_guidance)
        medicalGuidanceBtn.setOnClickListener {
            val intent = Intent(requireContext(), FirstAidActivity::class.java)
            startActivity(intent)
        }

        binding.fabAddMedicineReminder.setOnClickListener {
            startActivity(Intent(requireContext(),MedicineReminder::class.java))
        }

        return root
    }

    // Implement the OnDoctorClickListener interface method
    override fun onDoctorClick(doctor: DoctorData, position: Int) {
        // Create intent to open doctor profile
        val intent = Intent(requireContext(), DoctorProfileActivity::class.java).apply {
            // Pass doctor data to the profile activity
            putExtra("DOCTOR_NAME", doctor.name)
            putExtra("DOCTOR_SPECIALIZATION", doctor.specialization)
            putExtra("DOCTOR_IMAGE", doctor.imageUrl)
            // Add any other data you need to pass
        }
        startActivity(intent)
    }

    fun fetchAllDoctors(token: String) {
        val url = "https://medibot-8u6y.onrender.com/v1/api/doctor/all"
        (activity as? MainActivity)?.showLoading("Please wait...")

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                (activity as? MainActivity)?.hideLoading()
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Request failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                (activity as? MainActivity)?.hideLoading()
                if (response.isSuccessful) {
                    val gson = Gson()
                    val doctorResponse = gson.fromJson(responseBody, DoctorResponse::class.java)
                    doctorList = doctorResponse.doctors

                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Doctors fetched!", Toast.LENGTH_SHORT).show()
                        Log.e("Doctors", doctorList.joinToString { it.name })
                        // RecyclerView setup
                        val recyclerView: RecyclerView = binding.rvTopDocsProfiles
                        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 2 columns


                        // Set adapter with maxProfiles = 6 and pass this fragment as the click listener
                        val adapter = TopDocsAdapter(doctorList, maxProfiles = 6, this@HomeFragment)
                        recyclerView.adapter = adapter
                        val marginInPixels = resources.getDimensionPixelSize(R.dimen.item_bottom_margin)
                        val itemDecoration = MarginItemDecoration(0,0,0,marginInPixels)
                        recyclerView.addItemDecoration(itemDecoration)
                    }
                } else {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Failed to fetch doctors", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}