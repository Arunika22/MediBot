package com.example.myapplication.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * A utility class to handle SharedPreferences operations.
 * Provides methods to store, retrieve and delete different types of data.
 */
class SharedPreferencesUtils(context: Context, preferenceName: String = "app_preferences") {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)

    /**
     * Store a string value in SharedPreferences
     */
    fun putString(key: String, value: String) {
        sharedPreferences.edit {
            putString(key, value)
        }
    }

    /**
     * Store an integer value in SharedPreferences
     */
    fun putInt(key: String, value: Int) {
        sharedPreferences.edit {
            putInt(key, value)
        }
    }

    /**
     * Store a boolean value in SharedPreferences
     */
    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit {
            putBoolean(key, value)
        }
    }

    /**
     * Store a float value in SharedPreferences
     */
    fun putFloat(key: String, value: Float) {
        sharedPreferences.edit {
            putFloat(key, value)
        }
    }

    /**
     * Store a long value in SharedPreferences
     */
    fun putLong(key: String, value: Long) {
        sharedPreferences.edit {
            putLong(key, value)
        }
    }

    /**
     * Store a set of strings in SharedPreferences
     */
    fun putStringSet(key: String, value: Set<String>) {
        sharedPreferences.edit {
            putStringSet(key, value)
        }
    }

    /**
     * Get a string value from SharedPreferences
     * @return String value or default if key doesn't exist
     */
    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    /**
     * Get an integer value from SharedPreferences
     * @return Integer value or default if key doesn't exist
     */
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    /**
     * Get a boolean value from SharedPreferences
     * @return Boolean value or default if key doesn't exist
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    /**
     * Get a float value from SharedPreferences
     * @return Float value or default if key doesn't exist
     */
    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }

    /**
     * Get a long value from SharedPreferences
     * @return Long value or default if key doesn't exist
     */
    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    /**
     * Get a set of strings from SharedPreferences
     * @return Set of strings or default if key doesn't exist
     */
    fun getStringSet(key: String, defaultValue: Set<String> = emptySet()): Set<String> {
        return sharedPreferences.getStringSet(key, defaultValue) ?: defaultValue
    }

    /**
     * Check if SharedPreferences contains a specific key
     * @return true if key exists, false otherwise
     */
    fun contains(key: String): Boolean {
        return sharedPreferences.contains(key)
    }

    /**
     * Remove a specific value from SharedPreferences
     */
    fun remove(key: String) {
        sharedPreferences.edit {
            remove(key)
        }
    }

    /**
     * Clear all values in SharedPreferences
     */
    fun clear() {
        sharedPreferences.edit {
            clear()
        }
    }
}