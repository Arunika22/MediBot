package com.example.myapplication.activities.home.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activities.NotificationsActivity
import com.example.myapplication.activities.home.ui.doctor.DoctorFragment
import com.example.myapplication.adapters.TopDocsAdapter
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.models.DoctorData
import com.example.myapplication.utils.MarginItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
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
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 2 columns

        // Dummy Data
        val doctorList = arrayListOf(
            DoctorData("Dr. John Doe", "Cardiologist", "4.5"),
            DoctorData("Dr. Smith", "Neurologist", "4.7"),
            DoctorData("Dr. Alice", "Dermatologist", "4.8"),
            DoctorData("Dr. Bob", "Pediatrician", "4.6"),
            DoctorData("Dr. Sarah", "Gynecologist", "4.4"),
            DoctorData("Dr. Michael", "Dentist", "4.9")
        )

        // Set adapter with maxProfiles = 4 (Only 4 profiles displayed)
        val adapter = TopDocsAdapter(doctorList, maxProfiles = 6)
        recyclerView.adapter = adapter
        val marginInPixels = resources.getDimensionPixelSize(R.dimen.item_bottom_margin) // You can define this dimension in res/values/dimens.xml
        val itemDecoration = MarginItemDecoration(0,0,0,marginInPixels)
        recyclerView.addItemDecoration(itemDecoration)

        binding.btnNotifications.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationsActivity::class.java))
        }

        binding.tvSeeAllDocs.setOnClickListener {
//            navigate to a fragment using BottomNavigation
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
            // Set the Profile tab as selected
            bottomNav.selectedItemId = R.id.navigation_doctors
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}