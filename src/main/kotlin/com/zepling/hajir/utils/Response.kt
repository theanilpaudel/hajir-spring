package com.zepling.hajir.utils

sealed class Response<out T> {
    data class Success<out T>(val t: T) : Response<T>()
    data class Error(val message: String) : Response<Nothing>()
}