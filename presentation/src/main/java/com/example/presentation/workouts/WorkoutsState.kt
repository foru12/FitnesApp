package com.example.presentation.workouts

import com.example.domain.workout.Workout

sealed class WorkoutsState {
    object Loading : WorkoutsState()
    data class Success(val workouts: List<Workout>) : WorkoutsState()
    data class Error(val message: String) : WorkoutsState()
}
