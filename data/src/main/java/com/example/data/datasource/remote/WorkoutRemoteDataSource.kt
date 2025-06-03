package com.example.data.datasource.remote

import com.example.data.api.utils.ApiResponse
import com.example.domain.workout.VideoWorkout
import com.example.domain.workout.Workout

interface WorkoutRemoteDataSource {
    suspend fun getWorkouts(): ApiResponse<List<Workout>>
    suspend fun getVideoWorkout(id: Int): ApiResponse<VideoWorkout>
}