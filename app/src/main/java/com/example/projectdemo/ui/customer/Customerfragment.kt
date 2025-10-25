package com.example.projectdemo.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView // <-- Make sure this import is correct
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectdemo.databinding.FragmentCustomerBinding
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CustomerFragment : Fragment() {

    private var _binding: FragmentCustomerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CustomerViewModel by viewModels()
    private lateinit var customerAdapter: CustomerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFilters()
        setupSearchView() // <-- ADDED THIS
        observeUiState()
    }

    private fun setupRecyclerView() {
        customerAdapter = CustomerAdapter()
        binding.rvCustomers.apply {
            adapter = customerAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupFilters() {
        binding.chipGroupFilters.setOnCheckedChangeListener { group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)
            if (chip != null) {
                Toast.makeText(context, "Filter by: ${chip.text}", Toast.LENGTH_SHORT).show()
                // You could extend this by calling viewModel.setFilter(chip.text)
            }
        }
    }

    /**
     * NEW FUNCTION
     * Connects the SearchView UI to the ViewModel.
     */
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            // Called when the user types every character
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText.orEmpty())
                return true
            }

            // Called when the user presses the search button on the keyboard
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.setSearchQuery(query.orEmpty())
                binding.searchView.clearFocus() // Hides the keyboard
                return true
            }
        })
    }

    private fun observeUiState() {
        // *** COROUTINE USAGE (Unchanged but improved) ***
        // This function is now cleaner. It just observes the final state.
        // The complex logic of combining/filtering is handled in the ViewModel's coroutine.
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.isVisible = state is CustomerUiState.Loading
                binding.tvError.isVisible = state is CustomerUiState.Error
                binding.rvCustomers.isVisible = state is CustomerUiState.Success

                when (state) {
                    is CustomerUiState.Loading -> { /* Handled by visibility */ }
                    is CustomerUiState.Error -> binding.tvError.text = state.message
                    is CustomerUiState.Success -> {
                        customerAdapter.submitList(state.items)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}