package com.example.projectdemo.data.model

import com.google.gson.annotations.SerializedName

// Main data class for the appointment screen
data class AppointmentScreenData(
    @SerializedName("stats") val stats: AppointmentStats,
    @SerializedName("quickActions") val quickActions: List<AppointmentAction>
)

data class AppointmentStats(
    @SerializedName("upcoming") val upcoming: Int,
    @SerializedName("pending") val pending: Int,
    @SerializedName("completed") val completed: Int,
    @SerializedName("cancel") val cancel: Int
)

// This class definition was missing
data class AppointmentAction(
    @SerializedName("id") val id: String,
    @SerializedName("initials") val initials: String,
    @SerializedName("name") val name: String,
    @SerializedName("service") val service: String,
    @SerializedName("status") val status: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("date") val date: String,
    @SerializedName("time") val time: String,
    @SerializedName("price") val price: String
)