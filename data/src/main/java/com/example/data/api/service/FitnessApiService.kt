package com.example.data.api.service

import com.example.data.model.response.VideoWorkoutResponse
import com.example.data.model.response.WorkoutResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FitnessApiService {
    @GET("get_workouts")
    suspend fun getWorkouts(): Response<List<WorkoutResponse>>

    @GET("get_video")
    suspend fun getVideo(
        @Query("id") id: Int
    ): Response<VideoWorkoutResponse>
}