package com.example.presentation.workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.GetWorkoutsUseCase
import com.example.domain.util.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutsViewModel @Inject constructor(
    private val getWorkoutsUseCase: GetWorkoutsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<WorkoutsState>(WorkoutsState.Loading)
    val state: StateFlow<WorkoutsState> = _state

    init {
        loadWorkouts()
    }

    private fun loadWorkouts() {
        viewModelScope.launch {
            when (val result = getWorkoutsUseCase()) {
                is ResultState.Success -> _state.value = WorkoutsState.Success(result.data)
                is ResultState.Error -> _state.value = WorkoutsState.Error(result.message)
                is ResultState.Loading -> _state.value = WorkoutsState.Loading
            }
        }
    }

    fun refreshWorkouts() {
        viewModelScope.launch {
            _state.value = WorkoutsState.Loading
            when (val result = getWorkoutsUseCase()) {
                is ResultState.Success -> _state.value = WorkoutsState.Success(result.data)
                is ResultState.Error -> _state.value = WorkoutsState.Error(result.message)
                else -> {}
            }
        }
    }


}
