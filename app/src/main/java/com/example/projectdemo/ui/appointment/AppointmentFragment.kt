package com.example.projectdemo.ui.appointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectdemo.R
import com.example.projectdemo.data.model.AppointmentAction
import com.example.projectdemo.data.model.AppointmentScreenData
import com.example.projectdemo.databinding.FragmentAppointmentBinding
import com.example.projectdemo.databinding.ItemAppointmentStatBinding
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AppointmentFragment : Fragment() {

    private var _binding: FragmentAppointmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AppointmentViewModel by viewModels()
    private lateinit var quickActionAdapter: AppointmentQuickActionAdapter

    // To hold the complete list of appointments from the API
    private var allAppointments: List<AppointmentAction> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupTabs() // Set up the tabs and listener
        observeUiState() // Observe data from ViewModel
    }

    private fun setupRecyclerView() {
        quickActionAdapter = AppointmentQuickActionAdapter()
        binding.rvQuickActions.apply {
            adapter = quickActionAdapter
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
        }
    }

    private fun setupTabs() {
        // Add tabs
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Upcoming"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Pending"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Completed"))

        // Add listener for tab selection
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // When a tab is selected, filter the list
                filterAppointments(tab?.position ?: 0)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) { /* No action needed */ }
            override fun onTabReselected(tab: TabLayout.Tab?) { /* No action needed */ }
        })
    }

    private fun observeUiState() {
        // *** COROUTINE USAGE 3 (Unchanged) ***
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.isVisible = state is AppointmentUiState.Loading
                binding.tvError.isVisible = state is AppointmentUiState.Error
                binding.contentScrollView.isVisible = state is AppointmentUiState.Success

                when (state) {
                    is AppointmentUiState.Loading -> { /* Handled by visibility */ }
                    is AppointmentUiState.Error -> binding.tvError.text = state.message
                    is AppointmentUiState.Success -> bindAppointmentData(state.data)
                }
            }
        }
    }

    private fun bindAppointmentData(data: AppointmentScreenData) {
        // Bind the 4 stat cards (same as before)
        bindStatCard(
            binding.statUpcoming, data.stats.upcoming.toString(), "Upcoming",
            R.drawable.ic_appointments, R.drawable.shape_stat_background_upcoming, R.color.status_upcoming
        )
        bindStatCard(
            binding.statPending, data.stats.pending.toString(), "Pending",
            R.drawable.ic_pending, R.drawable.shape_stat_background_pending, R.color.status_pending
        )
        bindStatCard(
            binding.statCompleted, data.stats.completed.toString(), "Completed",
            R.drawable.ic_completed, R.drawable.shape_stat_background_completed, R.color.status_completed
        )
        bindStatCard(
            binding.statCancel, data.stats.cancel.toString(), "Cancel",
            R.drawable.ic_cancel, R.drawable.shape_stat_background_cancel, R.color.status_cancel
        )

        // *** NEW LOGIC ***
        // 1. Store the full list
        allAppointments = data.quickActions

        // 2. Filter the list based on the *currently selected* tab (which is 0 by default)
        filterAppointments(binding.tabLayout.selectedTabPosition)
    }

    /**
     * Filters the `allAppointments` list based on the selected tab position
     * and submits the result to the adapter.
     */
    private fun filterAppointments(position: Int) {
        // Get the status string based on tab position
        val statusToFilter = when (position) {
            0 -> "Upcoming"
            1 -> "Pending"
            2 -> "Completed"
            else -> "" // Should not happen
        }

        // Filter the full list
        val filteredList = if (statusToFilter.isEmpty()) {
            allAppointments // Should not happen, but as a fallback
        } else {
            allAppointments.filter { it.status == statusToFilter }
        }

        // Submit the new filtered list to the adapter
        quickActionAdapter.submitList(filteredList)
    }

    private fun bindStatCard(
        itemBinding: ItemAppointmentStatBinding,
        value: String,
        title: String,
        iconRes: Int,
        bgRes: Int,
        iconTintRes: Int
    ) {
        // ... (This function is unchanged from my previous response)
        itemBinding.tvStatValue.text = value
        itemBinding.tvStatTitle.text = title
        itemBinding.imgIcon.setImageResource(iconRes)
        itemBinding.iconContainer.setBackgroundResource(bgRes)
        itemBinding.imgIcon.setColorFilter(
            ContextCompat.getColor(requireContext(), iconTintRes)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}