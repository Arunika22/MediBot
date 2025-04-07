package com.example.myapplication.activities.home.ui.appointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.activities.home.MainActivity
import com.example.myapplication.activities.home.ui.appointment.utils.AppointmentApiClient
import com.example.myapplication.databinding.FragmentAppointmentBinding
import com.example.myapplication.databinding.ItemAppointmentBinding
import com.example.myapplication.models.Appointment
import com.example.myapplication.models.AppointmentResponse
import com.example.myapplication.utils.SharedPreferencesUtils
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AppointmentFragment : Fragment() {

    private var _binding: FragmentAppointmentBinding? = null
    private val binding get() = _binding!!

    private val upcomingAppointments = mutableListOf<Appointment>()
    private val pastAppointments = mutableListOf<Appointment>()
    private lateinit var appointmentAdapter: AppointmentAdapter
    private lateinit var appointmentApiClient: AppointmentApiClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferencesUtils=SharedPreferencesUtils(requireContext())
        val token = sharedPreferencesUtils.getString("token")
        appointmentApiClient = AppointmentApiClient(token)

        setupUI()
        fetchAppointments()

    }

    private fun setupUI() {
        // Setup tab layout
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> displayAppointments(upcomingAppointments)
                    1 -> displayAppointments(pastAppointments)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Setup RecyclerView
        binding.rvAppointments.layoutManager = LinearLayoutManager(requireContext())
        appointmentAdapter = AppointmentAdapter()
        binding.rvAppointments.adapter = appointmentAdapter

        // Show loading
        showLoading(true)
    }

    private fun fetchAppointments() {
        lifecycleScope.launch {
            try {
                val result = appointmentApiClient.fetchAllAppointments()

                if (result.isSuccess) {
                    val response = result.getOrNull()
                    if (response != null) {
                        processAppointmentData(response)
                    } else {
                        showError("Failed to load appointments")
                    }
                } else {
                    showError("Error: ${(result.exceptionOrNull()?.message ?: "Unknown error")}")
                }
            } catch (e: Exception) {
                showError("Exception: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun processAppointmentData(response: AppointmentResponse) {
        upcomingAppointments.clear()
        pastAppointments.clear()

        // Process all appointments by date
        response.appointments.forEach { (date, appointmentsForDate) ->
            appointmentsForDate.forEach { appointment ->
                if (appointment.status.equals("Upcoming", ignoreCase = true)) {
                    upcomingAppointments.add(appointment)
                } else {
                    pastAppointments.add(appointment)
                }
            }
        }

        // Update appointment counters
        binding.tvTotalAppointments.text = "Total: ${response.totalAppointments}"

        // Display appointments based on current tab
        val selectedTab = binding.tabLayout.selectedTabPosition
        when (selectedTab) {
            0 -> displayAppointments(upcomingAppointments)
            1 -> displayAppointments(pastAppointments)
            else -> {
                // By default, show upcoming appointments
                binding.tabLayout.getTabAt(0)?.select()
                displayAppointments(upcomingAppointments)
            }
        }
    }

    private fun displayAppointments(appointments: List<Appointment>) {
        appointmentAdapter.updateAppointments(appointments)

        // Show empty state if no appointments
        if (appointments.isEmpty()) {
            binding.rvAppointments.visibility = View.GONE
            binding.emptyStateLayout.visibility = View.VISIBLE
        } else {
            binding.rvAppointments.visibility = View.VISIBLE
            binding.emptyStateLayout.visibility = View.GONE
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            (activity as? MainActivity)?.showLoading("Loading Appointment...")
        } else {
            (activity as? MainActivity)?.hideLoading()
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        binding.emptyStateLayout.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // RecyclerView Adapter
    inner class AppointmentAdapter : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

        private val appointments = mutableListOf<Appointment>()

        fun updateAppointments(newAppointments: List<Appointment>) {
            appointments.clear()
            appointments.addAll(newAppointments)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
            val binding = ItemAppointmentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return AppointmentViewHolder(binding)
        }

        override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
            holder.bind(appointments[position])
        }

        override fun getItemCount() = appointments.size

        inner class AppointmentViewHolder(private val binding: ItemAppointmentBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(appointment: Appointment) {
                // Format the appointment ID to be shorter
                binding.tvAppointmentId.text = "Order ID: ${appointment.id.takeLast(8)}"

                // Set date and time
                binding.tvAppointmentDate.text = "Appointment: ${appointment.date}, ${appointment.timeSlot}"

                // Set doctor name
                binding.tvDoctorName.text = appointment.doctorDetails.name

                // Load doctor image if available
                if (appointment.doctorDetails.imageUrl != "N/A") {
                    Glide.with(binding.root)
                        .load(appointment.doctorDetails.imageUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.ic_profile_circle)
                        .error(R.drawable.ic_profile_circle)
                        .into(binding.ivDoctorImage)
                } else {
                    binding.ivDoctorImage.setImageResource(R.drawable.ic_profile_circle)
                }

                // Set specialization if available
                if (appointment.doctorDetails.specialization != "N/A") {
                    binding.tvDoctorSpecialization.visibility = View.VISIBLE
                    binding.tvDoctorSpecialization.text = appointment.doctorDetails.specialization
                } else {
                    binding.tvDoctorSpecialization.visibility = View.GONE
                }

                // Set patient name
                binding.tvPatientName.text = "Patient: ${appointment.patientName}"

                // Format booking timestamp
                val bookedDate = formatBookedDate(appointment.bookedAt)

                // Apply card styling based on status
                val cardBackground = if (appointment.status.equals("Upcoming", ignoreCase = true))
                    R.drawable.bg_card_upcoming else R.drawable.bg_card_past
                binding.cardAppointment.setBackgroundResource(cardBackground)
            }

            private fun formatBookedDate(dateString: String): String {
                return try {
                    // Assuming format like "4/7/2025, 6:16:21 AM"
                    // No need to reformat for this example, but you could parse and format differently
                    dateString
                } catch (e: Exception) {
                    ""
                }
            }
        }
    }
}