package com.example.projectdemo.ui.appointment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectdemo.data.model.AppointmentScreenData
import com.example.projectdemo.data.repository.HomeRepository
import com.example.projectdemo.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Sealed class for this screen's UI state
sealed class AppointmentUiState {
    data object Loading : AppointmentUiState()
    data class Success(val data: AppointmentScreenData) : AppointmentUiState()
    data class Error(val message: String) : AppointmentUiState()
}

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AppointmentUiState>(AppointmentUiState.Loading)
    val uiState: StateFlow<AppointmentUiState> = _uiState.asStateFlow()

    init {
        loadAppointmentData()
    }

    fun loadAppointmentData() {
        // *** COROUTINE USAGE 2 (New) ***
        // We launch a coroutine in the viewModelScope.
        viewModelScope.launch {
            _uiState.value = AppointmentUiState.Loading

            // Collect the flow from the repository's new function
            repository.getAppointmentData().collect { result ->
                when (result) {
                    is Result.Loading -> _uiState.value = AppointmentUiState.Loading
                    is Result.Success -> _uiState.value = AppointmentUiState.Success(result.data)
                    is Result.Error -> _uiState.value = AppointmentUiState.Error(result.message)
                }
            }
        }
    }
}