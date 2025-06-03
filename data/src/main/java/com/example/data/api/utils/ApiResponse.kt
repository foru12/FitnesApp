package com.example.data.api.utils

sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class Error(val exception: ApiException) : ApiResponse<Nothing>()
}