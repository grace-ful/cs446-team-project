package com.example.cs446_fit4me.ui.viewmodel

import MatchEntry
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cs446_fit4me.network.ApiClient
import kotlinx.coroutines.launch

class MatchingViewModel : ViewModel() {

    private val _matches = mutableStateListOf<MatchEntry>()
    val matches: List<MatchEntry> get() = _matches

    private var hasFetchedMatches = false

    fun fetchUserMatches(context: Context) {
        if (hasFetchedMatches) return

        viewModelScope.launch {
            try {
                val api = ApiClient.getMatchingApi(context)
                val result = api.getUserMatches()
                _matches.clear()
                _matches.addAll(result)
                hasFetchedMatches = true
                Log.d("MatchingViewModel", "Fetched ${_matches.size} matches.")
            } catch (e: Exception) {
                Log.e("MatchingViewModel", "Error fetching matches", e)
            }
        }
    }

    fun refreshMatchesManually(context: Context) {
        viewModelScope.launch {
            try {
                val api = ApiClient.getMatchingApi(context)

                // 1. Call POST /matches/update/{userId} before fetching new matches
                val userId = com.example.cs446_fit4me.datastore.UserManager.getUserId(context)
                if (userId != null) {
                    val response = api.updateMatches()
                    if (!response.isSuccessful) {
                        Log.e("MatchingViewModel", "Failed to update matches: ${response.code()}")
                    } else {
                        Log.d("MatchingViewModel", "Updated matches successfully.")
                    }
                }

                // 2. Then fetch the updated matches
                hasFetchedMatches = false
                fetchUserMatches(context)
            } catch (e: Exception) {
                Log.e("MatchingViewModel", "Error during manual refresh", e)
            }
        }
    }


    fun clearMatches() {
        _matches.clear()
        hasFetchedMatches = false
    }
}
