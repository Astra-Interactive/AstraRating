package com.astrainteractive.astrarating.exception

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.coroutineContext

interface ISealedExceptionHandler<T : Exception> {
    val clazz: Class<T>
    fun handle(e: T)
    fun intercept(block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            if (e::class.java.superclass == clazz)
                handle(e as T)
        }
    }

    suspend fun suspendIntercept(scope: CoroutineScope, block: suspend CoroutineScope.() -> Unit) {
        try {
            block(scope)
        } catch (e: Exception) {
            if (e::class.java.superclass == clazz)
                handle(e as T)
        }
    }
}