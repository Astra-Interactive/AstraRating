package ru.astrainteractive.astrarating.data.exposed.rating

import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.TimeSource

internal class RefreshingCache<K : Any, V : Any>(
    expiresAfterAccess: Duration = Duration.INFINITE,
    expireAfterWrite: Duration = Duration.INFINITE,
    maximumSize: Long = Long.MAX_VALUE,
    private val updateAfterAccess: Duration = Duration.INFINITE,
    private val coroutineScope: CoroutineScope,
    private val update: suspend (K) -> V?
) {
    private val cache: Cache<K, Entry<V>> = Cache.Builder<K, Entry<V>>()
        .maximumCacheSize(maximumSize)
        .expireAfterAccess(expiresAfterAccess)
        .expireAfterWrite(expireAfterWrite)
        .build()

    private suspend fun refreshAndGet(key: K): V? {
        return update.invoke(key)?.let { updated ->
            val entry = Entry(updated)
            cache.put(key, entry)
            entry.value
        }
    }

    fun refresh(key: K): Job = coroutineScope.launch {
        refreshAndGet(key)
    }

    fun getIfPresent(key: K): V? {
        val entry = cache.get(key)
        if (entry == null || entry.isOlderThan(updateAfterAccess)) refresh(key)
        return entry?.value
    }

    suspend fun get(key: K): V? {
        return getIfPresent(key) ?: refreshAndGet(key)
    }

    fun invalidateAll() {
        cache.invalidateAll()
    }

    private class Entry<T : Any>(val value: T) {
        private val cachedAt = TimeSource.Monotonic.markNow()

        fun isOlderThan(duration: Duration): Boolean = cachedAt.elapsedNow() > duration
    }
}
