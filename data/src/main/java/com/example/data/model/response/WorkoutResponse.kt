package com.example.data.model.response

data class WorkoutResponse(
    val id: Int,
    val title: String,
    val description: String?,
    val type: Int,
    val duration: String
)