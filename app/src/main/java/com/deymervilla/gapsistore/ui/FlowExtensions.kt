package com.deymervilla.gapsistore.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.collections.map
import kotlin.coroutines.cancellation.CancellationException

fun <T> Flow<Result<T>>.launchInViewModelScope(
    viewModel: ViewModel,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    onEach: suspend (Result<T>) -> Unit = {}
): Job = viewModel.viewModelScope.launch(dispatcher) {
    collect { result ->
        onEach(result)
    }
}

fun <T> Flow<Result<T>>.start(action: suspend () -> Unit): Flow<Result<T>> = onStart {
    action()
}

fun <T, R> Flow<Result<T>>.map(transform: suspend (T) -> R): Flow<Result<R>> = map { result ->
    result.fold(
        onSuccess = { data -> Result.success(transform(data)) },
        onFailure = { exception -> Result.failure(exception) }
    )
}

fun <T> Flow<Result<T>>.success(action: suspend (T) -> Unit): Flow<Result<T>> = onEach { result ->
    result.getOrNull()?.let { action(it) }
}

fun <T> Flow<Result<T>>.failure(action: suspend (Throwable) -> Unit): Flow<Result<T>> = onEach { result ->
    result.exceptionOrNull()?.let { action(it) }
}

inline fun <T, R, U> Flow<Result<T>>.attachAs(
    crossinline fetch: suspend (T) -> R?,
    crossinline build: (T, R?) -> U
): Flow<Result<U>> = map { result ->
    result.fold(
        onSuccess = { t ->
            runCatching {
                val extra = fetch(t)
                build(t, extra)
            }.fold(onSuccess = { Result.success(it) }, onFailure = { Result.failure(it) })
        },
        onFailure = { Result.failure(it) }
    )
}

inline fun <T, ITEM, R, U> Flow<Result<T>>.attachListAs(
    crossinline getItems: (T) -> List<ITEM>,
    crossinline fetch: suspend (ITEM) -> R?,
    crossinline build: (T, List<R>) -> U,
    parallel: Boolean = true,
    skipItemFailures: Boolean = true,
    maxConcurrency: Int = Int.MAX_VALUE
): Flow<Result<U>> = map { result ->
    result.fold(
        onSuccess = { t ->
            runCatchingNonCancellation {
                val items = getItems(t)
                if (items.isEmpty()) return@runCatchingNonCancellation Result.success(build(t,
                    emptyList()
                ))
                val extras: List<R> =
                    if (parallel) {
                        supervisorScope {
                            val sem = Semaphore(maxConcurrency.coerceAtLeast(1))
                            items.withIndex().map { (index, item) ->
                                async {
                                    sem.withPermit {
                                        runCatchingNonCancellation {
                                            fetch(item)?.let { index to it }
                                                ?: if (skipItemFailures) null
                                                else throw kotlin.IllegalStateException("Fetch returned null for item at index=$index")
                                        }.getOrElse { e ->
                                            if (skipItemFailures) null else throw e
                                        }
                                    }
                                }
                            }.awaitAll()
                                .filterNotNull()
                                .sortedBy { it.first }
                                .map { it.second }
                        }
                    } else {
                        buildList {
                            items.forEachIndexed { index, item ->
                                try {
                                    val r = fetch(item)
                                    if (r != null) add(r)
                                    else if (!skipItemFailures) {
                                        throw kotlin.IllegalStateException("Fetch returned null for item at index=$index")
                                    }
                                } catch (e: CancellationException) {
                                    throw e
                                } catch (e: Throwable) {
                                    if (!skipItemFailures) throw e
                                }
                            }
                        }
                    }

                Result.success(build(t, extras))
            }.getOrElse { e -> Result.failure(e) }
        },
        onFailure = { Result.failure(it) }
    )
}

inline fun <R> runCatchingNonCancellation(block: () -> R): Result<R> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (t: Throwable) {
        Result.failure(t)
    }

suspend fun Throwable.isIOEx(action: suspend () -> Unit) {
    if (this is java.io.IOException) action()
}

suspend fun Throwable.default(action: suspend () -> Unit) {
    action()
}

fun <T> Flow<T>.launchIn(
    scope: CoroutineScope,
    dispatcher: CoroutineDispatcher
): Job = scope.launch(dispatcher) {
    collect {}
}

fun <T> Flow<T>.withMinDelay(ms: Long): Flow<T> = onEach {
    delay(ms)
}