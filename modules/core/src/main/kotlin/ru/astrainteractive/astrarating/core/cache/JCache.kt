package ru.astrainteractive.astrarating.core.cache

import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private val cache: Cache<K, Data<V>> = Cache.Builder<K, Data<V>>()
        .maximumCacheSize(maximumSize)
        .expireAfterAccess(expiresAfterAccess)
        .build()

    private class Data<T : Any>(val data: T) {
        val cachedAt: Long = System.currentTimeMillis()
        val a: Any = 1

        fun needUpdate(duration: Duration): Boolean {
            val timePassed = System.currentTimeMillis().minus(cachedAt).milliseconds
            return timePassed > duration
        }
    }

    private suspend fun refreshAndGet(key: K) = withContext(limitedDispatcher) {
        val updated = update.invoke(this, key)
        val data = Data(updated)
        cache.put(key, data)
        data.data
    }

    private fun refresh(key: K) = coroutineScope.launch(limitedDispatcher) {
        refreshAndGet(key)
    }

    suspend fun get(key: K): V {
        return getIfPresent(key) ?: refreshAndGet(key)
    }

    fun getIfPresent(key: K): V? {
        val cacheData = cache.get(key)
        if (cacheData == null || cacheData.needUpdate(updateAfterAccess)) refresh(key)
        return cacheData?.data
    }
}
