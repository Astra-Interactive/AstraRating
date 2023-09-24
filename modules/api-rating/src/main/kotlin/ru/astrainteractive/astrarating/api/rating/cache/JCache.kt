package ru.astrainteractive.astrarating.api.rating.cache

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.kotlin.com.google.common.cache.Cache
import org.jetbrains.kotlin.com.google.common.cache.CacheBuilder
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class JCache<K : Any, V : Any>(
    expiresAfterAccess: Duration,
    private val updateAfterAccess: Duration,
    maximumSize: Long,
    private val coroutineScope: CoroutineScope,
    private val update: suspend CoroutineScope.(K) -> V
) {
    private val limitedDispatcher = Dispatchers.IO.limitedParallelism(1)
    private val cache: Cache<K, Data<V>> = CacheBuilder
        .newBuilder()
        .maximumSize(maximumSize)
        .expireAfterAccess(expiresAfterAccess.inWholeSeconds, TimeUnit.SECONDS)
        .build()

    private class Data<T : Any>(val data: T) {
        val cachedAt: Long = System.currentTimeMillis()

        fun needUpdate(duration: Duration): Boolean {
            val timePassed = System.currentTimeMillis().minus(cachedAt).milliseconds
            return timePassed > duration
        }
    }

    private fun refresh(key: K) = coroutineScope.launch(limitedDispatcher) {
        val updated = update.invoke(this, key)
        cache.put(key, Data(updated))
    }

    fun getIfPresent(key: K): V? {
        val cacheData = cache.getIfPresent(key)
        if (cacheData == null || cacheData.needUpdate(updateAfterAccess)) refresh(key)
        return cacheData?.data
    }
}
