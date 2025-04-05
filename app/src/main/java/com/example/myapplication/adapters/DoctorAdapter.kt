package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.models.Doctor

class DoctorAdapter(
    private val doctors: List<Doctor>,
    private val onDoctorClickListener: OnDoctorClickListener
) : RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    interface OnDoctorClickListener {
        fun onDoctorClick(doctor: Doctor)
        fun onBookClick(doctor: Doctor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        holder.bind(doctors[position])
    }

    override fun getItemCount(): Int = doctors.size

    inner class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivDoctorImage: ImageView = itemView.findViewById(R.id.ivDoctorImage)
        private val viewOnlineStatus: View = itemView.findViewById(R.id.viewOnlineStatus)
        private val tvDoctorName: TextView = itemView.findViewById(R.id.tvDoctorName)
        private val tvSpecialization: TextView = itemView.findViewById(R.id.tvSpecialization)
        private val tvExperience: TextView = itemView.findViewById(R.id.tvExperience)
        private val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val btnBook: Button = itemView.findViewById(R.id.btnBook)

        fun bind(doctor: Doctor) {
            tvDoctorName.text = doctor.name
            tvSpecialization.text = doctor.specialization
            tvExperience.text = doctor.experience
            tvRating.text = doctor.rating.toString()
            tvPrice.text = doctor.price.toString()

            // Show or hide online indicator
            viewOnlineStatus.visibility = if (doctor.isOnline) View.VISIBLE else View.GONE

            // Load doctor image
            Glide.with(itemView.context)
                .load(doctor.imageUrl)
                .placeholder(R.drawable.ic_profile_circle)
                .circleCrop()
                .into(ivDoctorImage)

            // Set click listeners
            itemView.setOnClickListener {
                onDoctorClickListener.onDoctorClick(doctor)
            }

            btnBook.setOnClickListener {
                onDoctorClickListener.onBookClick(doctor)
            }
        }
    }
}