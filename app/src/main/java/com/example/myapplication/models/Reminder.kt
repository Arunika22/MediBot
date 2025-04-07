package com.example.myapplication.models

data class Reminder(
    val medicineName: String,
    val dosage: String,
    val time: String,
    val days: List<String>
)
