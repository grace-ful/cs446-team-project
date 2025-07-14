package com.example.cs446_fit4me.datastore

import android.content.Context
import androidx.core.content.edit

object TokenManager {

    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        prefs.edit {
            putString("jwt", token)
        }
    }

    fun getToken(context: Context): String? {
        val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        return prefs.getString("jwt", null)
    }

    fun clearToken(context: Context) {
        val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        prefs.edit {
            remove("jwt")
        }
    }
}
