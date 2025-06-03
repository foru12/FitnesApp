package com.example.presentation.di

import com.example.domain.repo.WorkoutRepository
import com.example.domain.usecase.GetVideoWorkoutUseCase
import com.example.domain.usecase.GetWorkoutsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetWorkoutsUseCase(
        repository: WorkoutRepository
    ): GetWorkoutsUseCase = GetWorkoutsUseCase(repository)

    @Provides
    fun provideGetVideoWorkoutUseCase(
        repository: WorkoutRepository
    ): GetVideoWorkoutUseCase = GetVideoWorkoutUseCase(repository)
}