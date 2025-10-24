package com.example.projectdemo.utils

// A sealed class to represent UI states: Loading, Success, or Error
sealed class Result<out T> {
    data object Loading : Result<Nothing>()
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}