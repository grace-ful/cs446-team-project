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
        hasFetchedMatches = false
        fetchUserMatches(context)
    }

    fun clearMatches() {
        _matches.clear()
        hasFetchedMatches = false
    }
}
