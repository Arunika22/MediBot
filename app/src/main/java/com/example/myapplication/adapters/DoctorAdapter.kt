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
import com.example.myapplication.models.DoctorData
import kotlin.random.Random

class DoctorAdapter(
    private val doctors: ArrayList<DoctorData>,
    private val onDoctorClickListener: OnDoctorClickListener
) : RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    interface OnDoctorClickListener {
        fun onDoctorClick(doctor: DoctorData)
        fun onBookClick(doctor: DoctorData)
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

        fun bind(doctor: DoctorData) {
            tvDoctorName.text = doctor.name
            tvSpecialization.text = doctor.specialization
            val values = listOf(4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 4.7, 4.8)
            val randomValue = values.random()
            tvExperience.text =(3..6).random().toString()
            tvRating.text = randomValue.toString()
            tvPrice.text = "${(500..1000).random()}"

            // Show or hide online indicator
            val temp=false
            viewOnlineStatus.visibility = if (temp) View.VISIBLE else View.GONE

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

    fun getRandomUserUrl(): String {
        val randomNumber = Random.nextInt(1, 61) // 1 to 60 inclusive
        return "https://randomuser.me/api/portraits/men/$randomNumber.jpg"
    }
}