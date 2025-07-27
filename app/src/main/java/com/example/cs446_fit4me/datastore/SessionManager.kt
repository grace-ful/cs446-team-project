package com.example.cs446_fit4me.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map



data class Session(
    val userId: String?,
    val token: String?,
    val keepLoggedIn: Boolean
)

class SessionManager(private val context: Context) {

    companion object {
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val KEEP_LOGGED_IN_KEY = booleanPreferencesKey("keep_logged_in")
    }

    /**
     * Persist the session IF AND ONLY IF keepLoggedIn == true.
     * If the user did not check "keep me logged in", we clear anything that might be persisted.
     */
    suspend fun saveSession(userId: String, token: String, keepLoggedIn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
            prefs[ACCESS_TOKEN_KEY] = token
            prefs[KEEP_LOGGED_IN_KEY] = keepLoggedIn
        }
    }


    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(USER_ID_KEY)
            prefs.remove(ACCESS_TOKEN_KEY)
            prefs.remove(KEEP_LOGGED_IN_KEY)
        }
    }

    /** Stream the whole session */
    val sessionFlow: Flow<Session> = context.dataStore.data.map { prefs ->
        Session(
            userId = prefs[USER_ID_KEY],
            token = prefs[ACCESS_TOKEN_KEY],
            keepLoggedIn = prefs[KEEP_LOGGED_IN_KEY] == true
        )
    }

    /** True only when we actually persisted a token AND the user opted to keep logged in */
    val isPersistentlyLoggedInFlow: Flow<Boolean> = sessionFlow.map { session ->
        session.keepLoggedIn && !session.token.isNullOrBlank()
    }


    suspend fun getToken(): String? =
        context.dataStore.data.first()[ACCESS_TOKEN_KEY]

    suspend fun setKeepLoggedIn(keep: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEEP_LOGGED_IN_KEY] = keep
            if (!keep) {
                // If user disables it, also drop persisted secrets.
                prefs.remove(ACCESS_TOKEN_KEY)
            }
        }
    }
}
