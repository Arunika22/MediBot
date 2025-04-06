package com.example.myapplication.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.utils.LoadingDialog

/**
 * BaseActivity that all activities in the app should extend
 * Provides common functionality like loading dialog, permission handling, etc.
 */
abstract class BaseActivity : AppCompatActivity() {

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog = LoadingDialog(this)
    }

    /**
     * Shows a loading dialog with optional custom message
     * @param message The message to display in the loading dialog
     */
    fun showLoading(message: String = "Loading, please wait...") {
        runOnUiThread {
            loadingDialog.show(message)
        }
    }

    /**
     * Updates the message of the currently displayed loading dialog
     * @param message The new message to display
     */
    fun updateLoadingMessage(message: String) {
        runOnUiThread {
            if (loadingDialog.isShowing()) {
                loadingDialog.setMessage(message)
            }
        }
    }

    /**
     * Hides the loading dialog if it's currently showing
     */
    fun hideLoading() {
        runOnUiThread {
            loadingDialog.hide()
        }
    }

    /**
     * Check if loading dialog is currently showing
     * @return true if loading dialog is showing, false otherwise
     */
    fun isLoadingShowing(): Boolean {
        return loadingDialog.isShowing()
    }

    override fun onDestroy() {
        // Make sure to dismiss dialog to prevent window leaks
        if (loadingDialog.isShowing()) {
            loadingDialog.hide()
        }
        super.onDestroy()
    }
}