package com.oussama_chatri.core.base

sealed class UiState<out T> {

    data object Idle : UiState<Nothing>()

    data class Loading(val message: String = "Loading…") : UiState<Nothing>()

    data class Success<T>(val data: T) : UiState<T>()

    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : UiState<Nothing>()
}

val UiState<*>.isLoading: Boolean get() = this is UiState.Loading

val UiState<*>.isSuccess: Boolean get() = this is UiState.Success

val UiState<*>.isError: Boolean get() = this is UiState.Error

fun <T> UiState<T>.dataOrNull(): T? = (this as? UiState.Success)?.data

fun UiState<*>.errorMessageOrNull(): String? = (this as? UiState.Error)?.message