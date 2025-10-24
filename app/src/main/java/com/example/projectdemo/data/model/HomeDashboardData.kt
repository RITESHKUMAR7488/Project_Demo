package com.example.projectdemo.data.model

import com.google.gson.annotations.SerializedName

// Main data structure for the entire JSON
data class HomeDashboardData(
    @SerializedName("userName") val userName: String,
    @SerializedName("performanceOverview") val performanceOverview: PerformanceOverview,
    @SerializedName("earnings") val earnings: EarningsData,
    @SerializedName("todaySchedule") val todaySchedule: ScheduleData
)

data class PerformanceOverview(
    @SerializedName("todayBooking") val todayBooking: StatItem,
    @SerializedName("totalEarnings") val totalEarnings: StatItem,
    @SerializedName("totalCustomers") val totalCustomers: StatItem,
    @SerializedName("totalAppointments") val totalAppointments: StatItem
)

data class StatItem(
    @SerializedName("value") val value: String,
    @SerializedName("increase") val increase: String
)

data class EarningsData(
    @SerializedName("total") val total: String,
    @SerializedName("chartData") val chartData: List<ChartEntryData>
)

data class ChartEntryData(
    @SerializedName("x") val x: Float,
    @SerializedName("y") val y: Float
)

data class ScheduleData(
    @SerializedName("scheduledCount") val scheduledCount: Int,
    @SerializedName("appointments") val appointments: List<Appointment>
)

data class Appointment(
    @SerializedName("initials") val initials: String,
    @SerializedName("name") val name: String,
    @SerializedName("service") val service: String,
    @SerializedName("status") val status: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("date") val date: String,
    @SerializedName("time") val time: String,
    @SerializedName("price") val price: String
)