package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemReminderBinding
import com.example.myapplication.models.Reminder

class ReminderAdapter(private val reminders: List<Reminder>) :
    RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    inner class ReminderViewHolder(val binding: ItemReminderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        with(holder.binding) {
            tvMedicineName.text = reminder.medicineName
            tvDosage.text = "Dosage: ${reminder.dosage}"
            tvTime.text = "Time: ${reminder.time}"
            tvDays.text = "Days: ${reminder.days.joinToString(", ").replace("[", "").replace("]", "")}"
        }
    }

    override fun getItemCount() = reminders.size
}
