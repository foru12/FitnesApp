package com.example.presentation.video

import com.example.domain.workout.VideoWorkout

sealed class VideoWorkoutState {
    data object Loading : VideoWorkoutState()
    data class Success(val data: VideoWorkout) : VideoWorkoutState()
    data class Error(val message: String) : VideoWorkoutState()
}
