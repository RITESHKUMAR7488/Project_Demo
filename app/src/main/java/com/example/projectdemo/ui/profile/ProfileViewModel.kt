package com.example.projectdemo.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectdemo.data.model.Profile
import com.example.projectdemo.data.model.ProfileListItem
import com.example.projectdemo.data.model.ProfileScreenData
import com.example.projectdemo.data.repository.HomeRepository
import com.example.projectdemo.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Success(val items: List<ProfileListItem>) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    fun loadProfileData() {
        // *** COROUTINE USAGE 2 (New) ***
        // We use viewModelScope to launch a coroutine that is lifecycle-aware.
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            repository.getProfileData().collect { result ->
                when (result) {
                    is Result.Loading -> _uiState.value = ProfileUiState.Loading
                    is Result.Success -> {
                        // Process data to add the header
                        val processedList = processData(result.data)
                        _uiState.value = ProfileUiState.Success(processedList)
                    }
                    is Result.Error -> _uiState.value = ProfileUiState.Error(result.message)
                }
            }
        }
    }

    /**
     * Converts the data into a single list for the RecyclerView.
     * The Profile card will be the first item.
     */
    private fun processData(data: ProfileScreenData): List<ProfileListItem> {
        val items = mutableListOf<ProfileListItem>()
        // Add the profile header as the first item
        items.add(ProfileListItem.Header(data.profile))
        // Add all menu items
        data.menuItems.forEach {
            items.add(ProfileListItem.Item(it))
        }
        return items
    }
}