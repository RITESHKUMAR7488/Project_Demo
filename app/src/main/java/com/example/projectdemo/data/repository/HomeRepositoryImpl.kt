package com.example.projectdemo.data.repository

import android.content.Context
import com.example.projectdemo.data.model.HomeDashboardData
import com.example.projectdemo.utils.Result
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) : HomeRepository {

    override fun getHomeDashboardData(): Flow<Result<HomeDashboardData>> = flow {
        // Emit Loading state
        emit(Result.Loading)

        // Simulate network delay for 1.5 seconds to show the progress bar
        delay(1500)

        try {
            // Read JSON file from assets
            val jsonString = context.assets.open("HomeDashboard.json")
                .bufferedReader()
                .use { it.readText() }

            // Parse JSON string to data class
            val dashboardData = gson.fromJson(jsonString, HomeDashboardData::class.java)

            // Emit Success state with data
            emit(Result.Success(dashboardData))

        } catch (e: IOException) {
            // Emit Error state if file reading fails
            emit(Result.Error(e.message ?: "Error reading data"))
        } catch (e: Exception) {
            // Emit Error state for any other exceptions (e.g., JSON parsing)
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO) // *** COROUTINE USAGE 1 ***
}