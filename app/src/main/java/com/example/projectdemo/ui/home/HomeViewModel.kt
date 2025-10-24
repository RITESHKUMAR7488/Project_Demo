package com.example.projectdemo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectdemo.data.model.HomeDashboardData
import com.example.projectdemo.data.repository.HomeRepository
import com.example.projectdemo.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Sealed class to represent the different states of the Home screen
sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(val data: HomeDashboardData) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    // Private MutableStateFlow to hold the UI state
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)

    // Public non-mutable StateFlow for the Fragment to observe
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // Load data as soon as the ViewModel is created
        loadHomeDashboardData()
    }

    fun loadHomeDashboardData() {
        // *** COROUTINE USAGE 2 ***
        viewModelScope.launch {
            // Set state to Loading
            _uiState.value = HomeUiState.Loading

            // Collect the Flow from the repository
            repository.getHomeDashboardData().collect { result ->
                // Update the UI state based on the result from the repository
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = HomeUiState.Loading
                    }
                    is Result.Success -> {
                        _uiState.value = HomeUiState.Success(result.data)
                    }
                    is Result.Error -> {
                        _uiState.value = HomeUiState.Error(result.message)
                    }
                }
            }
        }
    }
}