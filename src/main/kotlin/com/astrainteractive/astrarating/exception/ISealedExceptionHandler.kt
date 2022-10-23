package com.astrainteractive.astrarating.exception

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.coroutineContext

interface ISealedExceptionHandler<T : Exception> {
    val clazz: Class<T>
    fun handle(e: T)
    fun <K> intercept(block: () -> K): K? {
        return try {
            block()
        } catch (e: Exception) {
            if (e::class.java.superclass == clazz)
                handle(e as T)
            null
        }
    }

    suspend fun <K> suspendIntercept(scope: CoroutineScope, block: suspend CoroutineScope.() -> K): K? {
        return try {
            block(scope)
        } catch (e: Exception) {
            if (e::class.java.superclass == clazz)
                handle(e as T)
            null
        }
    }
}