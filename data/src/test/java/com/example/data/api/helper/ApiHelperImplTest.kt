package com.example.data.api.helper

import com.example.data.api.utils.ApiException
import com.example.data.api.utils.ApiResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

class ApiHelperImplTest {

    private lateinit var helper: ApiHelperImpl

    @Before
    fun setUp() {
        helper = ApiHelperImpl()
    }

    @Test
    fun `safeApiCall returns Success when apiCall succeeds`() = runTest {
        val result = helper.safeApiCall { "Hello" }
        assertTrue(result is ApiResponse.Success)
        assertEquals("Hello", (result as ApiResponse.Success).data)
    }

    @Test
    fun `safeApiCall wraps HttpException into ApiResponse_Error`() = runTest {
        val httpException = HttpException(retrofit2.Response.error<String>(500, okhttp3.ResponseBody.create(null, "")))
        val result = helper.safeApiCall<String> { throw httpException }

        assertTrue(result is ApiResponse.Error)
        val err = (result as ApiResponse.Error).exception
        assertNotNull(err)
        assertEquals(500, err.code)
    }

    @Test
    fun `safeApiCall wraps IOException into ApiResponse_Error`() = runTest {
        val ioException = IOException("Network down")
        val result = helper.safeApiCall<String> { throw ioException }

        assertTrue(result is ApiResponse.Error)
        val err = (result as ApiResponse.Error).exception
        assertNull(err.code)
        assertEquals("Network error", err.message)
    }

    @Test
    fun `safeApiCall wraps generic Exception into ApiResponse_Error`() = runTest {
        val genException = Exception("Some generic")
        val result = helper.safeApiCall<String> { throw genException }

        assertTrue(result is ApiResponse.Error)
        val err = (result as ApiResponse.Error).exception
        assertNull(err.code)
        assertEquals("Some generic", err.message)
    }
}
