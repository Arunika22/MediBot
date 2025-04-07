package com.example.myapplication.activities.home.ui.appointment.utils

import android.util.Log
import com.example.myapplication.models.AppointmentResponse
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class AppointmentApiClient(private val authToken: String) {
    private val client = OkHttpClient()
    private val gson = Gson()
    private val TAG = "AppointmentApiClient"

    suspend fun fetchAllAppointments(): Result<AppointmentResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("https://medibot-8u6y.onrender.com/v1/api/appointment/all")
                    .addHeader("Authorization", "Bearer $authToken")
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val appointmentResponse = gson.fromJson(responseBody, AppointmentResponse::class.java)
                    Result.success(appointmentResponse)
                } else {
                    Log.e(TAG, "API Error: ${response.code} - ${response.message}")
                    Result.failure(IOException("API Error: ${response.code} - ${response.message}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during API call", e)
                Result.failure(e)
            }
        }
    }
}