package com.example.data.api.utils

class ApiException(
    val code: Int?,
    override val message: String?,
    val causeThrowable: Throwable? = null
) : Exception(message, causeThrowable)