package com.oussama_chatri.core.base

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.slf4j.LoggerFactory

abstract class BaseViewModel {

    protected val logger = LoggerFactory.getLogger(this::class.java)

    private val supervisorJob = SupervisorJob()

    protected val viewModelScope = CoroutineScope(
        Dispatchers.Main + supervisorJob
    )

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        logger.error("Uncaught coroutine exception in ${this::class.simpleName}", throwable)
        _globalError.value = throwable.message ?: "An unexpected error occurred."
    }

    protected val safeScope = CoroutineScope(
        Dispatchers.Main + supervisorJob + errorHandler
    )

    private val _globalError = MutableStateFlow<String?>(null)

    val globalError: StateFlow<String?> = _globalError.asStateFlow()

    fun clearGlobalError() {
        _globalError.value = null
    }

    open fun onCleared() {
        logger.debug("${this::class.simpleName} cleared — cancelling coroutine scope")
        viewModelScope.cancel()
        safeScope.cancel()
    }
}