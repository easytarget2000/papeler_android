package org.eztarget.papeler

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

open class SharedPreferenceStorage {

    private lateinit var sharedPreferences: SharedPreferences

    fun open(context: Context) {
        val appContext = context.applicationContext
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
    }

    internal fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    internal fun edit(key: String, value: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    internal fun edit(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }
}