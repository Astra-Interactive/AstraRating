package ru.astrainteractive.astrarating.core.cache

import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

interface KCache<K : Any, V : Any> {
    suspend fun get(key: K): V?

    fun getIfPresent(key: K): V?

    fun invalidateAll()
}

class Cache4kCache<K : Any, V : Any>(
    expiresAfterAccess: Duration = Duration.INFINITE,
    expireAfterWrite: Duration = Duration.INFINITE,
    maximumSize: Long = Long.MAX_VALUE,
    private val updateAfterAccess: Duration = Duration.INFINITE,
    private val coroutineScope: CoroutineScope,
    private val update: suspend (K) -> V?
) : KCache<K, V> {
    private val cache: Cache<K, Data<V>> = Cache.Builder<K, Data<V>>()
        .maximumCacheSize(maximumSize)
        .expireAfterAccess(expiresAfterAccess)
        .expireAfterWrite(expireAfterWrite)
        .build()

    private class Data<T : Any>(val data: T) {
        val cachedAt: Long = System.currentTimeMillis()

        fun needUpdate(duration: Duration): Boolean {
            val timePassed = System.currentTimeMillis().minus(cachedAt).milliseconds
            return timePassed > duration
        }
    }

    private suspend fun refreshAndGet(key: K): V? {
        val updated = update.invoke(key)
        return if (updated != null) {
            val data = Data(updated)
            cache.put(key, data)
            data.data
        } else {
            null
        }
    }

    private fun refresh(key: K) = coroutineScope.launch {
        refreshAndGet(key)
    }

    override suspend fun get(key: K): V? {
        return getIfPresent(key) ?: refreshAndGet(key)
    }

    override fun getIfPresent(key: K): V? {
        val cacheData = cache.get(key)
        if (cacheData == null || cacheData.needUpdate(updateAfterAccess)) refresh(key)
        return cacheData?.data
    }

    override fun invalidateAll() {
        cache.invalidateAll()
    }
}
