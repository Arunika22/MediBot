package com.example.myapplication.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMedicalReportAnalysisBinding
import com.example.myapplication.databinding.DialogAddMedicalReportBinding
import com.example.myapplication.utils.SharedPreferencesUtils
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MedicalReportAnalysis : AppCompatActivity() {

    private lateinit var binding: ActivityMedicalReportAnalysisBinding
    private var selectedFileUri: Uri? = null
    private val client = OkHttpClient()

    private val ENDPOINT_URL = "https://medibot-8u6y.onrender.com/v1/api/medicine/upload-report" // Temporary endpoint for testing

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedFileUri = it
            dialogBinding?.selectedFileTextView?.text = getFileNameFromUri(it)
        }
    }

    private var dialogBinding: DialogAddMedicalReportBinding? = null
    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicalReportAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        // Set up back button
        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        // Set up add record button
        binding.addRecordButton.setOnClickListener {
            checkPermissionsAndShowDialog()
        }
    }

    private fun checkPermissionsAndShowDialog() {
        // Different permission sets based on Android version
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

        Dexter.withContext(this)
            .withPermissions(permissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        showAddMedicalReportDialog()
                    } else {
                        Toast.makeText(
                            this@MedicalReportAnalysis,
                            "Storage permissions are required to select files",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun showAddMedicalReportDialog() {
        val builder = AlertDialog.Builder(this)
        dialogBinding = DialogAddMedicalReportBinding.inflate(LayoutInflater.from(this))

        builder.setView(dialogBinding?.root)
        dialog = builder.create()

        // Make dialog background transparent
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Set up close button
        dialogBinding?.closeDialogButton?.setOnClickListener {
            dialog?.dismiss()
        }

        // Set up browse button
        dialogBinding?.browseButton?.setOnClickListener {
            openFilePicker()
        }

        // Set up upload button
        dialogBinding?.uploadButton?.setOnClickListener {
            Log.d("UPLOAD_DEBUG", "Upload button clicked")

            uploadMedicalReport()
        }

        dialog?.show()
    }

    private fun openFilePicker() {
        getContent.launch("*/*") // Accept all file types
    }


    private fun resetUploadButton() {
        dialogBinding?.uploadButton?.isEnabled = true
        dialogBinding?.uploadButton?.text = "Upload"
    }

    private fun uploadMedicalReport() {
        if (selectedFileUri == null) {
            Toast.makeText(this, "Please select a file", Toast.LENGTH_SHORT).show()
            Log.e("UPLOAD_DEBUG", "selectedFileUri is null, not uploading")

            return
        }

        // Placeholder JWT for now — replace with actual token

        val jwtToken = SharedPreferencesUtils(this).getString("token", "")



        Log.d("JWT_DEBUG", "JWT Token: $jwtToken")

        // Form values (could also be editable fields in your dialog)
        val reportType = "blood_test"
        val description = "Regular blood test report"
        val date = "2024-04-05"

        dialogBinding?.uploadButton?.isEnabled = false
        dialogBinding?.uploadButton?.text = "Uploading..."

        try {
            val tempFile = createTempFileFromUri(selectedFileUri!!)
            Log.d("UPLOAD_DEBUG", "Temp file name: ${tempFile?.name}")
            Log.d("UPLOAD_DEBUG", "Temp file size: ${tempFile?.length()}")
            Log.d("UPLOAD_DEBUG", "JWT Token used: $jwtToken")
            Log.d("UPLOAD_DEBUG", "Selected file type: ${contentResolver.getType(selectedFileUri!!)}")



            if (tempFile != null) {
                val mimeType = contentResolver.getType(selectedFileUri!!)
                Log.d("UPLOAD_DEBUG", "Detected MIME type: $mimeType")

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "report",
                        tempFile.name,
                        tempFile.asRequestBody("application/pdf".toMediaTypeOrNull()) // or "image/*"
                    )

                    .addFormDataPart("reportType", reportType)
                    .addFormDataPart("description", description)
                    .addFormDataPart("date", date)
                    .build()

                val request = Request.Builder()
                    .url(ENDPOINT_URL)
                    .addHeader("Authorization", "Bearer $jwtToken")
                    .post(requestBody)
                    .build()
                Log.d("UPLOAD_DEBUG", "Request Headers: ${request.headers}")
                Log.d("UPLOAD_DEBUG", "Request URL: ${request.url}")
                Log.d("UPLOAD_DEBUG", "Request Method: ${request.method}")
                Log.d("UPLOAD_DEBUG", "Request Body Content Length: ${request.body?.contentLength()}")

                client.newCall(request).enqueue(object : Callback {

                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread {
                            Log.e("UPLOAD_DEBUG", "Failed to upload file", e)
                            Toast.makeText(
                                this@MedicalReportAnalysis,
                                "Failed to upload: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            resetUploadButton()
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        runOnUiThread {
                            if (response.isSuccessful) {
                                Log.d("UPLOAD_DEBUG", "Server returned error: ${response.code}")
                                val responseBody = response.body?.string()
                                Log.d("UPLOAD_DEBUG", "Raw response body: $responseBody")


                                try {
                                    val json = JSONObject(responseBody ?: "")
                                    val analysis = json.optJSONObject("analysis")

                                    val haemoglobinObj = analysis?.optJSONObject("haemoglobin")
                                    val sugarLevelObj = analysis?.optJSONObject("sugarLevel")

                                    val haemoglobinValue = haemoglobinObj?.optDouble("value", -1.0) ?: -1.0
                                    val haemoglobinMsg = haemoglobinObj?.optString("message", "No message") ?: "No message"

                                    val sugarLevelValue = sugarLevelObj?.optDouble("value", -1.0) ?: -1.0
                                    val sugarLevelMsg = sugarLevelObj?.optString("message", "No message") ?: "No message"

                                    val intent = Intent(this@MedicalReportAnalysis, MedicalReportResultActivity::class.java).apply {
                                        putExtra("haemoglobin_value", haemoglobinValue)
                                        putExtra("haemoglobin_msg", haemoglobinMsg)
                                        putExtra("sugarLevel_value", sugarLevelValue)
                                        putExtra("sugarLevel_msg", sugarLevelMsg)
                                    }

                                    startActivity(intent)


                                    dialog?.dismiss()

                                } catch (e: Exception) {
                                    Toast.makeText(this@MedicalReportAnalysis, "Error parsing response", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                val errorBody = response.body?.string()
                                Log.e("UPLOAD_DEBUG", "Upload failed: ${response.code}, body: $errorBody")

                                Toast.makeText(
                                    this@MedicalReportAnalysis,
                                    "Upload failed: ${response.code}",
                                    Toast.LENGTH_LONG
                                ).show()
                                resetUploadButton()
                            }
                        }
                    }
                })
            } else {
                Toast.makeText(this, "Could not process file.", Toast.LENGTH_LONG).show()
                resetUploadButton()
            }
        } catch (e: Exception) {
            Log.e("FileError", "Error processing file", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            resetUploadButton()
        }
    }

    private fun createTempFileFromUri(uri: Uri): File? {
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val fileName = getFileNameFromUri(uri)
            val tempFile = File(cacheDir, fileName)

            FileOutputStream(tempFile).use { outputStream ->
                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.flush()
            }
            inputStream.close()
            return tempFile
        } catch (e: Exception) {
            Log.e("FileError", "Error creating temp file", e)
            return null
        }
    }

    private fun getFileNameFromUri(uri: Uri): String {
        val contentResolver = applicationContext.contentResolver
        val cursor = contentResolver.query(uri, null, null, null, null)

        return cursor?.use {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            it.getString(nameIndex)
        } ?: "file_${System.currentTimeMillis()}"
    }
}