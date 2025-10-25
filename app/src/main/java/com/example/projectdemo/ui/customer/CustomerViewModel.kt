package com.example.projectdemo.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectdemo.data.model.CustomerListItem
import com.example.projectdemo.data.model.CustomerScreenData
import com.example.projectdemo.data.repository.HomeRepository
import com.example.projectdemo.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

// Sealed class (Unchanged)
sealed class CustomerUiState {
    data object Loading : CustomerUiState()
    data class Success(val items: List<CustomerListItem>) : CustomerUiState()
    data class Error(val message: String) : CustomerUiState()
}

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    // Holds the original data from the repository
    private val _rawData = MutableStateFlow<Result<CustomerScreenData>>(Result.Loading)

    // Holds the current search query from the UI
    private val _searchQuery = MutableStateFlow("")

    // The final combined state to be shown to the UI
    private val _uiState = MutableStateFlow<CustomerUiState>(CustomerUiState.Loading)
    val uiState: StateFlow<CustomerUiState> = _uiState.asStateFlow()

    init {
        // Start observing both data and search query
        observeDataAndQuery()
        // Fetch the initial data
        loadCustomerData()
    }

    // *** COROUTINE USAGE (MODIFIED) ***
    private fun observeDataAndQuery() {
        // `combine` is a powerful coroutine Flow operator.
        // This block will re-execute whenever _rawData or _searchQuery changes.
        viewModelScope.launch {
            _rawData.combine(_searchQuery) { result, query ->
                // This logic block processes the data
                when (result) {
                    is Result.Loading -> CustomerUiState.Loading
                    is Result.Error -> CustomerUiState.Error(result.message)
                    is Result.Success -> {
                        // Pass data and query to the filter function
                        val filteredList = filterAndProcessData(result.data, query)
                        CustomerUiState.Success(filteredList)
                    }
                }
            }.collect { combinedState ->
                // Emit the new state to the UI
                _uiState.value = combinedState
            }
        }
    }

    // This function just fetches data from the repository
    fun loadCustomerData() {
        // *** COROUTINE USAGE (Unchanged) ***
        viewModelScope.launch {
            _rawData.value = Result.Loading
            repository.getCustomerData().collect { result ->
                _rawData.value = result // Store the result
            }
        }
    }

    /**
     * This function is called by the Fragment to update the search query.
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * This function now handles both filtering and processing headers.
     */
    private fun filterAndProcessData(data: CustomerScreenData, query: String): List<CustomerListItem> {
        val items = mutableListOf<CustomerListItem>()

        data.customerGroups.forEach { group ->
            // 1. Filter the customers based on the query
            val filteredCustomers = group.customers.filter { customer ->
                customer.name.contains(query, ignoreCase = true) ||
                        customer.phone.contains(query, ignoreCase = true)
            }

            // 2. Only add the header and items if the filtered list is not empty
            if (filteredCustomers.isNotEmpty()) {
                items.add(CustomerListItem.HeaderItem(group.groupTitle))
                filteredCustomers.forEach { customer ->
                    items.add(CustomerListItem.CustomerItem(customer))
                }
            }
        }
        return items
    }
}