package com.zepling.hajir.utils


data class CustomResponse<out T>(
    val message: String? = null,
    val data: T? = null
)