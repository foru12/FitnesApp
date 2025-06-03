package com.example.data.datasource.remote

import com.example.data.api.helper.ApiHelper
import com.example.data.api.service.FitnessApiService
import com.example.data.api.utils.ApiException
import com.example.data.model.mapper.toDomain
import com.example.data.api.utils.ApiResponse
import com.example.domain.workout.VideoWorkout
import com.example.domain.workout.Workout
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRemoteDataSourceImpl @Inject constructor(
    private val apiService: FitnessApiService,
    private val apiHelper: ApiHelper
) : WorkoutRemoteDataSource {

    override suspend fun getWorkouts(): ApiResponse<List<Workout>> =
        apiHelper.safeApiCall {
            val response = apiService.getWorkouts()
            if (response.isSuccessful) {
                response.body()?.map { it.toDomain() }
                    ?: throw ApiException(response.code(), "Пустой ответ от сервера")
            } else {
                throw ApiException(response.code(), response.message())
            }
        }

    override suspend fun getVideoWorkout(id: Int): ApiResponse<VideoWorkout> =
        apiHelper.safeApiCall {
            val response = apiService.getVideo(id)
            if (response.isSuccessful) {
                response.body()?.toDomain()
                    ?: throw ApiException(response.code(), "Пустой ответ от сервера")
            } else {
                throw ApiException(response.code(), response.message())
            }
        }
}
