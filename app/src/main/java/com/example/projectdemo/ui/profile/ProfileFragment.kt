package com.example.projectdemo.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectdemo.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var profileAdapter: ProfileAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeUiState()
    }

    private fun setupRecyclerView() {
        profileAdapter = ProfileAdapter()
        binding.rvProfileItems.apply {
            adapter = profileAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeUiState() {
        // *** COROUTINE USAGE 3 (New) ***
        // We use viewLifecycleOwner.lifecycleScope.launch to safely collect
        // the StateFlow, ensuring UI updates only happen when the view is active.
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.isVisible = state is ProfileUiState.Loading
                binding.tvError.isVisible = state is ProfileUiState.Error
                binding.rvProfileItems.isVisible = state is ProfileUiState.Success

                when (state) {
                    is ProfileUiState.Loading -> { /* Handled by visibility */ }
                    is ProfileUiState.Error -> binding.tvError.text = state.message
                    is ProfileUiState.Success -> {
                        profileAdapter.submitList(state.items)
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