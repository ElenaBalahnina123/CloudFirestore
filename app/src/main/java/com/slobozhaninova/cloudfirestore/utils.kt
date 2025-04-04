package com.slobozhaninova.cloudfirestore

sealed class Result<out T> {
    data class Success<out T>(val value: T) : Result<T>()
    data class Failure(val exception: Exception) : Result<Nothing>()
}

fun <T> Result<T>.getOrThrow(): T {
    return when (this) {
        is Result.Success -> value
        is Result.Failure -> throw exception
    }
}