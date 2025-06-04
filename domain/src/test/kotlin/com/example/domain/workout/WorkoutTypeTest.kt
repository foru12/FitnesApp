package com.example.domain.workout


import org.junit.Assert.assertEquals
import org.junit.Test

class WorkoutTypeTest {

    @Test
    fun `fromCode returns correct enum for valid codes`() {
        assertEquals(WorkoutType.TRAINING, WorkoutType.fromCode(1))
        assertEquals(WorkoutType.LIVE,     WorkoutType.fromCode(2))
        assertEquals(WorkoutType.COMPLEX,  WorkoutType.fromCode(3))
    }

    @Test
    fun `fromCode returns TRAINING for invalid code`() {
        // 0 и 99 — не входят в 1,2,3
        assertEquals(WorkoutType.TRAINING, WorkoutType.fromCode(0))
        assertEquals(WorkoutType.TRAINING, WorkoutType.fromCode(99))
    }
}
