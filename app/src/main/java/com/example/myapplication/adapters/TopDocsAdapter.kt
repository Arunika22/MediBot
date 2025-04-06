package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapters.TopDocsAdapter.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.models.DoctorData

class TopDocsAdapter(
    var profileList: ArrayList<DoctorData>,
    private val maxProfiles: Int,
    private val onDoctorClickListener: OnDoctorClickListener
) : RecyclerView.Adapter<ViewHolder>() {

    // Interface for click events
    interface OnDoctorClickListener {
        fun onDoctorClick(doctor: DoctorData, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.layout_doctor_card, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return minOf(profileList.size, maxProfiles)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentUser = profileList[position]
        holder.personName.text = currentUser.name
        holder.personTitle.text = currentUser.title
        holder.tvRating.text = currentUser.rating

        // Load image with Glide
        val imageUrl = "https://imgs.search.brave.com/Tw_Wh1rl8hVTb9XRQ7rLFYstwaRA10ZhizRuDztxPPg/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly90My5m/dGNkbi5uZXQvanBn/LzAyLzk1LzUxLzgw/LzM2MF9GXzI5NTUx/ODA1Ml9hTzVkOUNx/UmhQbmpsTkRUUkRq/S0xaSE5mdHFmc3h6/SS5qcGc"

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .apply(RequestOptions()
                .placeholder(R.drawable.doctor_image1) // Add a placeholder drawable
                .error(R.drawable.doctor_image1) // Add an error drawable
                .centerCrop()) // Optional: Apply circle crop if your CircleImageView doesn't do this automatically
            .into(holder.cvProfileImg)

        // Set click listener on the entire item view
        holder.itemView.setOnClickListener {
            onDoctorClickListener.onDoctorClick(currentUser, position)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvProfileImg: CircleImageView = itemView.findViewById(R.id.cv_profile_img)
        val personName: TextView = itemView.findViewById(R.id.person_name)
        val personTitle: TextView = itemView.findViewById(R.id.person_title)
        val tvRating: TextView = itemView.findViewById(R.id.tv_rating)
    }
}