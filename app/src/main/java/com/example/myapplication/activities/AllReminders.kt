package com.example.myapplication.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.ReminderAdapter
import com.example.myapplication.databinding.ActivityAllRemindersBinding
import com.example.myapplication.models.Reminder
import com.example.myapplication.utils.SharedPreferencesUtils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class AllReminders : BaseActivity() {

    private lateinit var binding: ActivityAllRemindersBinding
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllRemindersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        fetchReminders()
    }

    private fun fetchReminders() {
        showLoading("Fetching Reminders...")
        val sharedPreferencesUtils= SharedPreferencesUtils(this)
        val token = sharedPreferencesUtils.getString("token")
        val request = Request.Builder()
            .url("https://medibot-8u6y.onrender.com/v1/api/medicine/reminders/all")
            .addHeader("Authorization", "Bearer ${token}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@AllReminders, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                hideLoading()
                response.body?.let { responseBody ->
                    val json = JSONObject(responseBody.string())
                    val remindersArray = json.getJSONArray("reminders")
                    Log.e("response all reminder", remindersArray.toString())
                    val remindersList = mutableListOf<Reminder>()

                    for (i in 0 until remindersArray.length()) {
                        val item = remindersArray.getJSONObject(i)

                        val name = item.getString("medicineName")
                        val dosage = item.getString("dosage")
                        val time = item.getString("time")

                        val daysJsonArray = item.getJSONArray("days")
                        val days = mutableListOf<String>()
                        for (j in 0 until daysJsonArray.length()) {
                            days.add(daysJsonArray.getString(j).replace("[", "").replace("]", ""))
                        }

                        remindersList.add(Reminder(name, dosage, time, days))
                    }

                    runOnUiThread {
                        if (remindersList.isEmpty()) {
                            Toast.makeText(this@AllReminders, "No reminders found", Toast.LENGTH_SHORT).show()
                        } else {
                            binding.recyclerView.layoutManager = LinearLayoutManager(this@AllReminders)
                            binding.recyclerView.adapter = ReminderAdapter(remindersList)
                        }

                    }
                }
            }
        })
    }
}
