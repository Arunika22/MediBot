package com.example.myapplication.models

import com.google.gson.annotations.SerializedName

// Main response model from API
data class AppointmentResponse(
    val message: String,
    val appointments: Map<String, List<Appointment>>,
    val totalAppointments: Int,
    val upcomingAppointments: Int,
    val pastAppointments: Int
)

// Appointment model
data class Appointment(
    val id: String,
    val doctorDetails: DoctorDetails,
    val patientName: String,
    val date: String,
    @SerializedName("rawDate")
    val rawDate: String,
    val timeSlot: String,
    val status: String,
    val bookedAt: String
) {
    // Constructor for simplified creation
    constructor(
        id: String,
        doctorName: String,
        doctorSpecialization: String,
        doctorImageUrl: String,
        patientName: String,
        date: String,
        timeSlot: String,
        status: String,
        bookedAt: String
    ) : this(
        id = id,
        doctorDetails = DoctorDetails(
            name = doctorName,
            specialization = doctorSpecialization,
            contactNumber = "N/A",
            email = "N/A",
            imageUrl = doctorImageUrl
        ),
        patientName = patientName,
        date = date,
        rawDate = "", // Usually set from API
        timeSlot = timeSlot,
        status = status,
        bookedAt = bookedAt
    )
}

// Doctor details model
data class DoctorDetails(
    val name: String,
    val specialization: String,
    val contactNumber: String,
    val email: String,
    val imageUrl: String
)