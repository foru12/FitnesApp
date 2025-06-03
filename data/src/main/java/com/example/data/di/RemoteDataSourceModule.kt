package com.example.data.di

import com.example.data.api.helper.ApiHelper
import com.example.data.api.service.FitnessApiService
import com.example.data.datasource.remote.WorkoutRemoteDataSource
import com.example.data.datasource.remote.WorkoutRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RemoteDataSourceModule {

    @Provides
    fun provideWorkoutRemoteDataSource(
        apiService: FitnessApiService,
        apiHelper: ApiHelper
    ): WorkoutRemoteDataSource = WorkoutRemoteDataSourceImpl(apiService, apiHelper)
}
