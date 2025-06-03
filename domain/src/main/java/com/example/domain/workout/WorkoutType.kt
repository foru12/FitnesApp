package com.example.domain.workout

enum class WorkoutType(val code: Int) {
    TRAINING(1),
    LIVE(2),
    COMPLEX(3);

    companion object {
        fun fromCode(code: Int): WorkoutType = entries.first { it.code == code }
    }
}