// data/src/test/java/com/example/data/datasource/remote/WorkoutRemoteDataSourceImplTest.kt
package com.example.data.datasource.remote

import com.example.data.api.helper.ApiHelperImpl
import com.example.data.api.service.FitnessApiService
import com.example.data.api.utils.ApiException
import com.example.data.api.utils.ApiResponse
import com.example.domain.workout.Workout
import com.example.domain.workout.WorkoutType
import com.example.domain.workout.VideoWorkout
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutRemoteDataSourceImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: FitnessApiService
    private lateinit var dataSource: WorkoutRemoteDataSourceImpl

    @Before
    fun setUp() {
        mockWebServer = MockWebServer().apply { start() }
        val baseUrl = mockWebServer.url("/").toString()
        apiService = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FitnessApiService::class.java)

        dataSource = WorkoutRemoteDataSourceImpl(apiService, ApiHelperImpl())
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getWorkouts returns Success when server responds 200 with valid JSON`() = runTest {
        val json = """
            [
              {"id":1,"title":"Workout A","description":"Desc A","type":1,"duration":"10"},
              {"id":2,"title":"Workout B","description":"Desc B","type":2,"duration":"20"}
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(json)
        )

        val result = dataSource.getWorkouts()
        assertTrue(result is ApiResponse.Success)
        val list = (result as ApiResponse.Success).data
        assertEquals(2, list.size)
        assertEquals("Workout A", list[0].title)
        assertEquals(WorkoutType.LIVE, list[1].type)
    }

    @Test
    fun `getWorkouts returns Error when server responds 500`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Server error")
        )

        val result = dataSource.getWorkouts()
        assertTrue(result is ApiResponse.Error)
        val exception = (result as ApiResponse.Error).exception
        // Должно быть именно ApiException (wrapped)
        assertTrue(exception is ApiException)
    }

    @Test
    fun `getVideoWorkout returns Success when server responds 200 with valid JSON`() = runTest {
        val json = """
            {"id":7,"duration":"120","link":"/videos/7.mp4"}
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(json)
        )

        val result = dataSource.getVideoWorkout(7)
        assertTrue(result is ApiResponse.Success)
        val video = (result as ApiResponse.Success).data
        assertEquals(7, video.id)
        assertEquals("120", video.duration)
        assertEquals("/videos/7.mp4", video.link)
    }

    @Test
    fun `getVideoWorkout returns Error when response body is empty`() = runTest {
        // Сервер возвращает 200, но без тела
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("")  // пустое тело
        )

        val result = dataSource.getVideoWorkout(7)
        assertTrue(result is ApiResponse.Error)
        val exception = (result as ApiResponse.Error).exception
        assertTrue(exception is ApiException)
    }

    @Test
    fun `getVideoWorkout returns Error when server responds 404`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
        )

        val result = dataSource.getVideoWorkout(7)
        assertTrue(result is ApiResponse.Error)
        val exception = (result as ApiResponse.Error).exception
        assertTrue(exception is ApiException)
    }
}
