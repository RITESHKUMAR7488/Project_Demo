package com.example.projectdemo.data.repository

import android.content.Context
import com.example.projectdemo.data.model.AppointmentScreenData
import com.example.projectdemo.data.model.HomeDashboardData
import com.example.projectdemo.utils.Result
import com.example.projectdemo.data.model.CustomerScreenData
import com.example.projectdemo.data.model.ProfileScreenData
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
        // ... (existing code) ...
        emit(Result.Loading)
        delay(1500) // Simulate delay
        try {
            val jsonString = context.assets.open("HomeDashboard.json")
                .bufferedReader()
                .use { it.readText() }
            val dashboardData = gson.fromJson(jsonString, HomeDashboardData::class.java)
            emit(Result.Success(dashboardData))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)

    // *** COROUTINE USAGE 1 (New) ***
    // This is the new function to get appointment data
    override fun getAppointmentData(): Flow<Result<AppointmentScreenData>> = flow {
        emit(Result.Loading)
        delay(1000) // Simulate a slightly different delay
        try {
            // Read the new JSON file
            val jsonString = context.assets.open("AppointmentScreen.json")
                .bufferedReader()
                .use { it.readText() }
            val appointmentData = gson.fromJson(jsonString, AppointmentScreenData::class.java)
            emit(Result.Success(appointmentData))
        } catch (e: IOException) {
            emit(Result.Error(e.message ?: "Error reading data"))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO) // We use Dispatchers.IO for the same reason: file reading is a blocking operation.

    override fun getCustomerData(): Flow<Result<CustomerScreenData>> = flow {
        emit(Result.Loading)
        delay(1200) // Simulate delay
        try {
            val jsonString = context.assets.open("Customers.json")
                .bufferedReader()
                .use { it.readText() }
            val customerData = gson.fromJson(jsonString, CustomerScreenData::class.java)
            emit(Result.Success(customerData))
        } catch (e: IOException) {
            emit(Result.Error(e.message ?: "Error reading data"))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO) // We use Dispatchers.IO because file reading is a blocking operation.

    override fun getProfileData(): Flow<Result<ProfileScreenData>> = flow {
        emit(Result.Loading)
        delay(800) // Simulate delay
        try {
            val jsonString = context.assets.open("Profile.json")
                .bufferedReader()
                .use { it.readText() }
            val profileData = gson.fromJson(jsonString, ProfileScreenData::class.java)
            emit(Result.Success(profileData))
        } catch (e: IOException) {
            emit(Result.Error(e.message ?: "Error reading data"))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO) // We use Dispatchers.IO for the file reading.
}