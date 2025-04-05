package com.example.myapplication.models

data class Doctor(
    val id: String,
    val name: String,
    val specialization: String,
    val experience: String,
    val rating: Float,
    val price: Double,
    val isOnline: Boolean,
    val imageUrl: String
)
