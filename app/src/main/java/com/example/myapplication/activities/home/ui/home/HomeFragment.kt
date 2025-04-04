package com.example.myapplication.activities.home.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.TopDocsAdapter
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.models.DoctorData
import com.example.myapplication.utils.MarginItemDecoration
import com.example.medibot.SymptomCheckerActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // RecyclerView setup
        val recyclerView: RecyclerView = binding.rvTopDocsProfiles
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val doctorList = arrayListOf(
            DoctorData("Dr. John Doe", "Cardiologist", "4.5"),
            DoctorData("Dr. Smith", "Neurologist", "4.7"),
            DoctorData("Dr. Alice", "Dermatologist", "4.8"),
            DoctorData("Dr. Bob", "Pediatrician", "4.6"),
            DoctorData("Dr. Sarah", "Gynecologist", "4.4"),
            DoctorData("Dr. Michael", "Dentist", "4.9")
        )

        val adapter = TopDocsAdapter(doctorList, maxProfiles = 6)
        recyclerView.adapter = adapter

        val marginInPixels = resources.getDimensionPixelSize(R.dimen.item_bottom_margin)
        val itemDecoration = MarginItemDecoration(0, 0, 0, marginInPixels)
        recyclerView.addItemDecoration(itemDecoration)

        // 🔹 Set click listener for the symptom checker card
        val symptomCheckerBtn = root.findViewById<CardView>(R.id.btn_symptoms_checker)
        symptomCheckerBtn.setOnClickListener {
            val intent = Intent(requireContext(), SymptomCheckerActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
