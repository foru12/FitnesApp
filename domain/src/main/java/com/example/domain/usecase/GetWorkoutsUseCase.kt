package com.example.domain.usecase

import com.example.domain.repo.WorkoutRepository
import com.example.domain.util.ResultState
import com.example.domain.workout.Workout

class GetWorkoutsUseCase (
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(): ResultState<List<Workout>> {
        return repository.getWorkouts()
    }
}
