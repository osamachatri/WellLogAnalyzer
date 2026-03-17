package com.oussama_chatri.core.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun CoroutineScope.launchIO(block: suspend CoroutineScope.() -> Unit): Job =
    launch(Dispatchers.IO, block = block)

fun CoroutineScope.launchDefault(block: suspend CoroutineScope.() -> Unit): Job =
    launch(Dispatchers.Default, block = block)

suspend fun <T> withIO(block: suspend CoroutineScope.() -> T): T =
    withContext(Dispatchers.IO, block)

suspend fun <T> withDefault(block: suspend CoroutineScope.() -> T): T =
    withContext(Dispatchers.Default, block)

suspend fun <T> withMain(block: suspend CoroutineScope.() -> T): T =
    withContext(Dispatchers.Main, block)