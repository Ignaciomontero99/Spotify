package com.nachomontero.spotify.sharedPreferences

import android.content.Context
import androidx.core.content.edit

object SessionManager {
    private const val PREFS_NAME = "user_session"
    private const val USER_ID_KEY = "user_id"
    private const val USER_TYPE_KEY = "user_type"

    fun saveSession(context: Context, userId: String, userType: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString(USER_ID_KEY, userId)
            putString(USER_TYPE_KEY, userType)
            apply()
        }
    }


    fun getUserId(context: Context): Int = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getInt(USER_ID_KEY, -1)

    fun getUserType(context: Context): String? = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getString(USER_TYPE_KEY, null)

    fun clearSession(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit() {
            clear()
        }
    }
}

