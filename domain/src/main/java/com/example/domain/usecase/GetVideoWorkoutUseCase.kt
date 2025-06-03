package com.example.domain.usecase

import com.example.domain.repo.WorkoutRepository
import com.example.domain.util.ResultState
import com.example.domain.workout.VideoWorkout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetVideoWorkoutUseCase(
    private val repository: WorkoutRepository
) {
    operator fun invoke(id: Int): Flow<ResultState<VideoWorkout>> = flow {
        emit(ResultState.Loading)
        try {
            val result = repository.getVideoWorkout(id)
            emit(result)
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Unknown error"))
        }
    }
}