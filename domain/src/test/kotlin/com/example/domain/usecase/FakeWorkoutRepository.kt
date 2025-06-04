package com.example.domain.usecase


import com.example.domain.repo.WorkoutRepository
import com.example.domain.util.ResultState
import com.example.domain.workout.Workout


class FakeWorkoutRepository(
    private val result: ResultState<List<Workout>>
) : WorkoutRepository {
    override suspend fun getWorkouts(): ResultState<List<Workout>> {
        return result
    }

    override suspend fun getVideoWorkout(id: Int): ResultState<com.example.domain.workout.VideoWorkout> {
        throw NotImplementedError("Не используется в этих тестах")
    }
}
