package com.example.presentation.workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.GetWorkoutsUseCase
import com.example.domain.util.ResultState
import com.example.domain.workout.Workout
import com.example.domain.workout.WorkoutType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutsViewModel @Inject constructor(
    private val getWorkoutsUseCase: GetWorkoutsUseCase
) : ViewModel() {

    private val _allWorkouts = MutableStateFlow<List<Workout>>(emptyList())
    private val _query = MutableStateFlow("")
    private val _selectedType = MutableStateFlow<WorkoutType?>(null)
    private val _loading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val isLoading: StateFlow<Boolean> = _loading.asStateFlow()

    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    @OptIn(FlowPreview::class)
    private val debouncedQuery: Flow<String> = _query
        .debounce(300)
        .distinctUntilChanged()

    val filteredWorkouts: StateFlow<List<Workout>> =
        combine(_allWorkouts, debouncedQuery, _selectedType) { all, query, type ->
            all.filter { workout ->
                val matchesQuery = workout.title.contains(query, ignoreCase = true)
                val matchesType = type?.let { workout.type == it } ?: true
                matchesQuery && matchesType
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    init {
        loadWorkouts()
    }

    fun setQuery(newQuery: String) {
        _query.value = newQuery.trim()
    }

    fun setType(type: WorkoutType?) {
        _selectedType.value = type
    }

    fun refreshWorkouts() {
        loadWorkouts()
    }

    private fun loadWorkouts() {
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null

            getWorkoutsUseCase()
                .onEach { result ->
                    when (result) {
                        is ResultState.Loading -> {
                        }
                        is ResultState.Success -> {
                            _allWorkouts.value = result.data
                        }
                        is ResultState.Error -> {
                            _errorMessage.value = result.message
                        }
                    }
                }
                .catch { throwable ->
                    _errorMessage.value = throwable.message ?: "Неизвестная ошибка"
                }
                .collect()
            _loading.value = false
        }
    }

}
