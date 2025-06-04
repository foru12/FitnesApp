package com.example.domain.usecase

import com.example.domain.repo.WorkoutRepository
import com.example.domain.util.ResultState
import com.example.domain.workout.Workout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetWorkoutsUseCase(
    private val repository: WorkoutRepository
) {
    operator fun invoke(): Flow<ResultState<List<Workout>>> = flow {
        emit(ResultState.Loading)
        when (val result = repository.getWorkouts()) {
            is ResultState.Success -> emit(ResultState.Success(result.data))
            is ResultState.Error   -> emit(ResultState.Error(result.message))
            is ResultState.Loading -> {  }
        }
    }
}
