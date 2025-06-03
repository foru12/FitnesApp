package com.example.data.model.mapper

import com.example.data.model.response.VideoWorkoutResponse
import com.example.data.model.response.WorkoutResponse
import com.example.domain.workout.VideoWorkout
import com.example.domain.workout.Workout
import com.example.domain.workout.WorkoutType

fun WorkoutResponse.toDomain(): Workout = Workout(
    id = id,
    title = title,
    description = description,
    type = WorkoutType.fromCode(type),
    duration = duration
)

fun VideoWorkoutResponse.toDomain(): VideoWorkout = VideoWorkout(
    id = id,
    duration = duration,
    link = link
)