package com.example.myapplication.activities.home.ui.doctor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.DoctorAdapter
import com.example.myapplication.databinding.FragmentDoctorBinding
import com.example.myapplication.models.Doctor

class DoctorFragment : Fragment(), DoctorAdapter.OnDoctorClickListener {

    private lateinit var rvDoctors: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var doctorAdapter: DoctorAdapter

    // Sample data - In real app, this would come from API or database
    private val doctorsList = listOf(
        Doctor(
            id = "1",
            name = "Dr. Ahmed Khan",
            specialization = "Cardiologist",
            experience = "2 years",
            rating = 4.8f,
            price = 500.00,
            isOnline = true,
            imageUrl = "https://example.com/doctor1.jpg"
        ),
        Doctor(
            id = "2",
            name = "Dr. Emma Kathrin",
            specialization = "Cardiologist",
            experience = "4 years",
            rating = 4.8f,
            price = 500.00,
            isOnline = true,
            imageUrl = "https://example.com/doctor2.jpg"
        ),
        Doctor(
            id = "3",
            name = "Dr. Emy Branton",
            specialization = "Cardiologist",
            experience = "3 years",
            rating = 4.9f,
            price = 500.00,
            isOnline = true,
            imageUrl = "https://example.com/doctor3.jpg"
        ),
        Doctor(
            id = "4",
            name = "Dr. Warner Miller",
            specialization = "Cardiologist",
            experience = "2 years",
            rating = 4.7f,
            price = 500.00,
            isOnline = true,
            imageUrl = "https://example.com/doctor4.jpg"
        )
    )

    private var filteredDoctors = ArrayList<Doctor>()

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

        // Set up RecyclerView
        setupRecyclerView()

        // Set up search functionality
        setupSearch()
    }

    private fun setupRecyclerView() {
        filteredDoctors.addAll(doctorsList)
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
            filteredDoctors.addAll(doctorsList)
        } else {
            val searchQuery = query.lowercase()
            doctorsList.forEach { doctor ->
                if (doctor.name.lowercase().contains(searchQuery) ||
                    doctor.specialization.lowercase().contains(searchQuery)) {
                    filteredDoctors.add(doctor)
                }
            }
        }

        doctorAdapter.notifyDataSetChanged()
    }

    override fun onDoctorClick(doctor: Doctor) {
        // Navigate to doctor details screen
        Toast.makeText(context, "Selected: ${doctor.name}", Toast.LENGTH_SHORT).show()
        startActivity(Intent(requireContext(),DoctorProfileActivity::class.java))
    }

    override fun onBookClick(doctor: Doctor) {
        // Navigate to booking screen
        Toast.makeText(context, "Booking with ${doctor.name}", Toast.LENGTH_SHORT).show()
    }
}