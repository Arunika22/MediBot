package com.example.myapplication.activities.home.ui.doctor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activities.home.MainActivity
import com.example.myapplication.adapters.DoctorAdapter
import com.example.myapplication.models.Doctor
import com.example.myapplication.models.DoctorData
import com.example.myapplication.models.DoctorResponse
import com.example.myapplication.utils.SharedPreferencesUtils
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class DoctorFragment : Fragment(), DoctorAdapter.OnDoctorClickListener {

    private lateinit var rvDoctors: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var doctorAdapter: DoctorAdapter
    private lateinit var doctorList: List<DoctorData>

//    // Sample data - In real app, this would come from API or database
//    private val doctorsList = listOf(
//        Doctor(
//            id = "1",
//            name = "Dr. Ahmed Khan",
//            specialization = "Cardiologist",
//            experience = "2 years",
//            rating = 4.8f,
//            price = 500.00,
//            isOnline = true,
//            imageUrl = "https://example.com/doctor1.jpg"
//        ),
//        Doctor(
//            id = "2",
//            name = "Dr. Emma Kathrin",
//            specialization = "Cardiologist",
//            experience = "4 years",
//            rating = 4.8f,
//            price = 500.00,
//            isOnline = true,
//            imageUrl = "https://example.com/doctor2.jpg"
//        ),
//        Doctor(
//            id = "3",
//            name = "Dr. Emy Branton",
//            specialization = "Cardiologist",
//            experience = "3 years",
//            rating = 4.9f,
//            price = 500.00,
//            isOnline = true,
//            imageUrl = "https://example.com/doctor3.jpg"
//        ),
//        Doctor(
//            id = "4",
//            name = "Dr. Warner Miller",
//            specialization = "Cardiologist",
//            experience = "2 years",
//            rating = 4.7f,
//            price = 500.00,
//            isOnline = true,
//            imageUrl = "https://example.com/doctor4.jpg"
//        )
//    )

    private var filteredDoctors = ArrayList<DoctorData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_doctor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        rvDoctors = view.findViewById(R.id.rvDoctors)
        etSearch = view.findViewById(R.id.etSearch)


        val sharedPreferencesUtils= SharedPreferencesUtils(requireContext())
        val token = sharedPreferencesUtils.getString("token")
        fetchAllDoctors(token)

        // Set up search functionality
        setupSearch()
    }

    private fun setupRecyclerView() {
        filteredDoctors.addAll(doctorList)
        doctorAdapter = DoctorAdapter(filteredDoctors, this)

        rvDoctors.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = doctorAdapter
        }
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener { text ->
            filterDoctors(text.toString())
        }
    }

    private fun filterDoctors(query: String) {
        filteredDoctors.clear()

        if (query.isEmpty()) {
            filteredDoctors.addAll(doctorList)
        } else {
            val searchQuery = query.lowercase()
            doctorList.forEach { doctor ->
                if (doctor.name.lowercase().contains(searchQuery) ||
                    doctor.specialization.lowercase().contains(searchQuery)) {
                    filteredDoctors.add(doctor)
                }
            }
        }

        doctorAdapter.notifyDataSetChanged()
    }

    override fun onDoctorClick(doctor: DoctorData) {
        // Navigate to doctor details screen
        Toast.makeText(context, "Selected: ${doctor.name}", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), DoctorProfileActivity::class.java).apply {
            // Pass doctor data to the profile activity
            putExtra("DOCTOR_NAME", doctor.name)
            putExtra("DOCTOR_SPECIALIZATION", doctor.specialization)
            putExtra("DOCTOR_IMAGE", doctor.imageUrl)
            // Add any other data you need to pass
        }
        startActivity(intent)
    }

    override fun onBookClick(doctor: DoctorData) {
        // Navigate to booking screen
        Toast.makeText(context, "Booking with ${doctor.name}", Toast.LENGTH_SHORT).show()
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
                       setupRecyclerView()
                    }
                } else {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Failed to fetch doctors", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}