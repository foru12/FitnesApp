package com.example.presentation.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.GetVideoWorkoutUseCase
import com.example.domain.util.ResultState
import com.example.domain.workout.VideoWorkout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class VideoWorkoutViewModel @Inject constructor(
    private val getVideoWorkoutUseCase: GetVideoWorkoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<VideoWorkoutState>(VideoWorkoutState.Loading)
    val uiState: StateFlow<VideoWorkoutState> = _uiState.asStateFlow()

    fun loadVideo(id: Int) {
        viewModelScope.launch {
            getVideoWorkoutUseCase(id)
                .onStart {
                    _uiState.value = VideoWorkoutState.Loading
                }
                .catch { exception ->
                    _uiState.value = VideoWorkoutState.Error(exception.message ?: "Unknown error")
                }
                .collect { result ->
                    when (result) {
                        is ResultState.Success -> {
                            _uiState.value = VideoWorkoutState.Success(result.data)
                        }
                        is ResultState.Error -> {
                            _uiState.value = VideoWorkoutState.Error(result.message)
                        }
                        is ResultState.Loading -> {
                            _uiState.value = VideoWorkoutState.Loading
                        }
                    }
                }
        }
    }
}
