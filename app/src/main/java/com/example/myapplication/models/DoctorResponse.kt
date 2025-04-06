package com.example.myapplication.models

data class DoctorResponse(
    val message: String,
    val doctors: List<DoctorData>,
    val count: Int
)