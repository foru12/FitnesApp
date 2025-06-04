package com.example.data.model.mapper

import com.example.data.model.response.WorkoutResponse
import com.example.data.model.response.VideoWorkoutResponse
import com.example.domain.workout.WorkoutType
import org.junit.Assert.assertEquals
import org.junit.Test

class MapperTest {

    @Test
    fun `WorkoutResponse maps to domain Workout correctly`() {
        val response = WorkoutResponse(
            id = 42,
            title = "Test Title",
            description = "Test Desc",
            type = 2,
            duration = "30"
        )

        val domain = response.toDomain()
        assertEquals(42, domain.id)
        assertEquals("Test Title", domain.title)
        assertEquals("Test Desc", domain.description)
        assertEquals(WorkoutType.LIVE, domain.type)
        assertEquals("30", domain.duration)
    }

    @Test
    fun `VideoWorkoutResponse maps to domain VideoWorkout correctly`() {
        val response = VideoWorkoutResponse(
            id = 7,
            duration = "120",
            link = "/videos/7.mp4"
        )

        val domain = response.toDomain()
        assertEquals(7, domain.id)
        assertEquals("120", domain.duration)
        assertEquals("/videos/7.mp4", domain.link)
    }
}
