package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapters.TopDocsAdapter.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import android.widget.TextView
import com.example.myapplication.R
import com.example.myapplication.models.DoctorData

class TopDocsAdapter(var profileList:ArrayList<DoctorData>,private val maxProfiles: Int) : RecyclerView.Adapter<ViewHolder>() {



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
        holder.personName.text=currentUser.name
        holder.personTitle.text=currentUser.title
        holder.tvRating.text=currentUser.rating
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val cvProfileImg: CircleImageView = itemView.findViewById(R.id.cv_profile_img)
		val personName: TextView = itemView.findViewById(R.id.person_name)
		val personTitle: TextView = itemView.findViewById(R.id.person_title)
		val tvRating: TextView = itemView.findViewById(R.id.tv_rating)
    }
}
