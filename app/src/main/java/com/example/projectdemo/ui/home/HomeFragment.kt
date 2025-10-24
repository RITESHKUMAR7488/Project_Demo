package com.example.projectdemo.ui.home

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
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
import com.example.projectdemo.data.model.ChartEntryData
import com.example.projectdemo.data.model.HomeDashboardData
import com.example.projectdemo.data.model.StatItem
import com.example.projectdemo.databinding.FragmentHomeBinding
import com.example.projectdemo.databinding.ItemPerformanceStatBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var scheduleAdapter: AppointmentScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeUiState()
    }

    private fun setupRecyclerView() {
        scheduleAdapter = AppointmentScheduleAdapter()
        binding.rvTodaySchedule.apply {
            adapter = scheduleAdapter
            layoutManager = LinearLayoutManager(context)
            // Disable nested scrolling for smoother scrolling within NestedScrollView
            isNestedScrollingEnabled = false
        }
    }

    private fun observeUiState() {
        // *** COROUTINE USAGE 3 ***
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                // Show/hide views based on the state
                binding.progressBar.isVisible = state is HomeUiState.Loading
                binding.tvError.isVisible = state is HomeUiState.Error
                binding.contentScrollView.isVisible = state is HomeUiState.Success

                when (state) {
                    is HomeUiState.Loading -> {
                        // Handled by visibility property
                    }
                    is HomeUiState.Error -> {
                        binding.tvError.text = state.message
                    }
                    is HomeUiState.Success -> {
                        bindDashboardData(state.data)
                    }
                }
            }
        }
    }

    private fun bindDashboardData(data: HomeDashboardData) {
        // Bind Toolbar
        binding.toolbarHome.tvUserName.text = data.userName
        binding.toolbarHome.tvAvatarInitials.text = data.userName
            .split(" ")
            .take(2)
            .map { it.firstOrNull()?.uppercase() }
            .joinToString("")

        // Bind Performance Stats
        bindStatItem(binding.statTodayBooking, "Today's Booking", data.performanceOverview.todayBooking)
        bindStatItem(binding.statTotalEarnings, "Total Earnings", data.performanceOverview.totalEarnings)
        bindStatItem(binding.statTotalCustomers, "Total Customers", data.performanceOverview.totalCustomers)
        bindStatItem(binding.statTotalAppt, "Appt", data.performanceOverview.totalAppointments)

        // Bind Earnings Chart
        binding.tvEarningsTotal.text = data.earnings.total
        setupLineChart(binding.lineChartEarnings, data.earnings.chartData)

        // Bind Today's Schedule
        binding.tvScheduleSubtitle.text =
            "you have ${data.todaySchedule.scheduledCount} appointments scheduled"
        scheduleAdapter.submitList(data.todaySchedule.appointments)
    }

    private fun bindStatItem(itemBinding: ItemPerformanceStatBinding, title: String, stat: StatItem) {
        itemBinding.tvStatTitle.text = title
        itemBinding.tvStatValue.text = stat.value
        itemBinding.tvStatIncrease.text = stat.increase
    }

    private fun setupLineChart(chart: LineChart, data: List<ChartEntryData>) {
        val entries = data.map { Entry(it.x, it.y) }
        val lineColor = ContextCompat.getColor(requireContext(), R.color.chart_line_color)

        val dataSet = LineDataSet(entries, "Earnings").apply {
            color = lineColor
            valueTextColor = ContextCompat.getColor(requireContext(), R.color.md_theme_light_onSurface)
            lineWidth = 2.5f
            setDrawCircles(false)
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER

            // Fill Gradient
            setDrawFilled(true)
            val gradientStart = ContextCompat.getColor(requireContext(), R.color.chart_fill_gradient_start)
            val gradientEnd = ContextCompat.getColor(requireContext(), R.color.chart_fill_gradient_end)
            val gradient = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(gradientStart, gradientEnd)
            )
            fillDrawable = gradient
        }

        chart.apply {
            this.data = LineData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(false)
            setDrawGridBackground(false)

            // X-Axis
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
                textColor = ContextCompat.getColor(requireContext(), R.color.text_secondary_light)
                granularity = 1f
            }

            // Y-Axis
            axisLeft.apply {
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setDrawLabels(false)
            }
            axisRight.isEnabled = false

            // Refresh chart
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}