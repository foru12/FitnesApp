package com.example.data.repo

import com.example.data.datasource.remote.WorkoutRemoteDataSource
import com.example.data.api.utils.ApiResponse
import com.example.domain.repo.WorkoutRepository
import com.example.domain.util.ResultState
import com.example.domain.workout.VideoWorkout
import com.example.domain.workout.Workout
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepositoryImpl @Inject constructor(
    private val remoteDataSource: WorkoutRemoteDataSource
) : WorkoutRepository {

    override suspend fun getWorkouts(): ResultState<List<Workout>> {
        return when (val response = remoteDataSource.getWorkouts()) {
            is ApiResponse.Success -> {
                ResultState.Success(response.data)
            }
            is ApiResponse.Error -> {
                val ex = response.exception
                val message = if (ex.code != null) {
                    "Сервер вернул ошибку ${ex.code}: ${ex.message}"
                } else {
                    "Ошибка сети: ${ex.message}"
                }
                ResultState.Error(message)
            }
        }
    }

    override suspend fun getVideoWorkout(id: Int): ResultState<VideoWorkout> {
        return when (val response = remoteDataSource.getVideoWorkout(id)) {
            is ApiResponse.Success -> ResultState.Success(response.data)
            is ApiResponse.Error -> {
                val ex = response.exception
                val message = if (ex.code != null) {
                    "Сервер вернул ошибку ${ex.code}: ${ex.message}"
                } else {
                    "Ошибка сети: ${ex.message}"
                }
                ResultState.Error(message)
            }
        }
    }
}

