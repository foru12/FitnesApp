package com.example.data.api.helper

import com.example.data.api.utils.ApiResponse


interface ApiHelper {
    suspend fun <T> safeApiCall(apiCall: suspend () -> T): ApiResponse<T>
}
