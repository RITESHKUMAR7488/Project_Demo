package com.example.projectdemo.data.repository

import com.example.projectdemo.data.model.HomeDashboardData
import com.example.projectdemo.utils.Result
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getHomeDashboardData(): Flow<Result<HomeDashboardData>>
}