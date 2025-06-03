package com.example.data.api.helper


import com.example.data.api.utils.ApiException
import com.example.data.api.utils.ApiResponse
import java.io.IOException
import javax.inject.Inject
import retrofit2.HttpException
import javax.inject.Singleton

@Singleton
class ApiHelperImpl @Inject constructor() : ApiHelper {
    override suspend fun <T> safeApiCall(apiCall: suspend () -> T): ApiResponse<T> {
        return try {
            val result = apiCall()
            ApiResponse.Success(result)
        } catch (e: HttpException) {
            ApiResponse.Error(
                ApiException(e.code(), e.message(), e)
            )
        } catch (e: IOException) {
            ApiResponse.Error(
                ApiException(null, "Network error", e)
            )
        } catch (e: Exception) {
            ApiResponse.Error(
                ApiException(null, e.localizedMessage ?: "Unknown error", e)
            )
        }
    }
}
