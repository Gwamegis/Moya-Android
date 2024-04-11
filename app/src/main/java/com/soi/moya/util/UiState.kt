package com.soi.moya.util

sealed class UiState<out T> {
    data class Success<T>(val data: T) : UiState<T>()
    data class Failure(val error: String) : UiState<Nothing>()
}