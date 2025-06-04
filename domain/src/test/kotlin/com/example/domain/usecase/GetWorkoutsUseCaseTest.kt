package com.example.domain.usecase


import app.cash.turbine.test
import com.example.domain.util.ResultState
import com.example.domain.workout.Workout
import com.example.domain.workout.WorkoutType
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class GetWorkoutsUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke emits Loading then Success`() = runTest {
        val fakeList = listOf(
            Workout(1, "A", "DescA", WorkoutType.TRAINING, "10"),
            Workout(2, "B", "DescB", WorkoutType.LIVE, "15")
        )
        val fakeRepo = FakeWorkoutRepository(ResultState.Success(fakeList))
        val useCase = GetWorkoutsUseCase(fakeRepo)

        useCase()
            .test {
                val first  = awaitItem()
                assert(first is ResultState.Loading)

                val second = awaitItem()
                assert(second is ResultState.Success)
                assertEquals(fakeList, (second as ResultState.Success).data)

                awaitComplete()
            }
    }

    @Test
    fun `invoke emits Loading then Error`() = runTest {
        val errorMessage = "Network error"
        val fakeRepo = FakeWorkoutRepository(ResultState.Error(errorMessage))
        val useCase = GetWorkoutsUseCase(fakeRepo)

        useCase()
            .test {
                val first  = awaitItem()
                assert(first is ResultState.Loading)

                val second = awaitItem()
                assert(second is ResultState.Error)
                assertEquals(errorMessage, (second as ResultState.Error).message)

                awaitComplete()
            }
    }
}
