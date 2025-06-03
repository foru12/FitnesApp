package com.example.domain.repo

import com.example.domain.util.ResultState
import com.example.domain.workout.VideoWorkout
import com.example.domain.workout.Workout

interface WorkoutRepository {
    suspend fun getWorkouts(): ResultState<List<Workout>>
    suspend fun getVideoWorkout(id: Int): ResultState<VideoWorkout>
}