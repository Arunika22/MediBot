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
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MedicalReportAnalysis : AppCompatActivity() {

    private lateinit var binding: ActivityMedicalReportAnalysisBinding
    private var selectedFileUri: Uri? = null
    private val client = OkHttpClient()
    private val ENDPOINT_URL = "https://httpbin.org/post" // Temporary endpoint for testing

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
           // uploadMedicalReport()
        }

        dialog?.show()
    }

    private fun openFilePicker() {
        getContent.launch("*/*") // Accept all file types
    }

    private fun uploadMedicalReport() {
        val email = dialogBinding?.emailEditText?.text.toString()

        // Validate email
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate file selection
        if (selectedFileUri == null) {
            Toast.makeText(this, "Please select a file", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading state
        dialogBinding?.uploadButton?.isEnabled = false
        dialogBinding?.uploadButton?.text = "Uploading..."

        // Create a temporary file from the URI
        try {
            val tempFile = createTempFileFromUri(selectedFileUri!!)

            if (tempFile != null) {
                // Create multipart request
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("email", email)
                    .addFormDataPart(
                        "file",
                        tempFile.name,
                        tempFile.asRequestBody("application/octet-stream".toMediaTypeOrNull())
                    )
                    .build()

                val request = Request.Builder()
                    .url(ENDPOINT_URL)
                    .post(requestBody)
                    .build()

                // Make network request
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread {
                            Log.e("UploadError", "Failed to upload file", e)
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
                                Toast.makeText(
                                    this@MedicalReportAnalysis,
                                    "Report uploaded successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                dialog?.dismiss()
                            } else {
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
                Toast.makeText(
                    this,
                    "Could not process file. Please try again.",
                    Toast.LENGTH_LONG
                ).show()
                resetUploadButton()
            }
        } catch (e: Exception) {
            Log.e("FileError", "Error processing file", e)
            Toast.makeText(
                this,
                "Error processing file: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            resetUploadButton()
        }
    }

    private fun resetUploadButton() {
        dialogBinding?.uploadButton?.isEnabled = true
        dialogBinding?.uploadButton?.text = "Upload"
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