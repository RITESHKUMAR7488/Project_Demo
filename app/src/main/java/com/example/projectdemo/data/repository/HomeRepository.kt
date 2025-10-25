package com.example.projectdemo.data.repository

import com.example.projectdemo.data.model.AppointmentScreenData
import com.example.projectdemo.data.model.CustomerScreenData
import com.example.projectdemo.data.model.HomeDashboardData
import com.example.projectdemo.utils.Result
import kotlinx.coroutines.flow.Flow
import com.example.projectdemo.data.model.ProfileScreenData


interface HomeRepository {
    fun getHomeDashboardData(): Flow<Result<HomeDashboardData>>
    fun getAppointmentData(): Flow<Result<AppointmentScreenData>>

    // Add this new function
    fun getCustomerData(): Flow<Result<CustomerScreenData>>
    fun getProfileData(): Flow<Result<ProfileScreenData>>
}