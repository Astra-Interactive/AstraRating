package com.astrainteractive.astrarating.domain.api

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.kotlin.com.google.common.cache.CacheBuilder
import ru.astrainteractive.astralibs.async.PluginScope
import java.util.*
import java.util.concurrent.TimeUnit

class CachedApiImpl(
    private val databaseApi: RatingDBApi,
    dispatcher: CoroutineDispatcher
) : CachedApi {

    private val cached = CacheBuilder
        .newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .build<UUID, PlayerData>()

    private val limitedDispatcher = dispatcher.limitedParallelism(1)

    @JvmInline
    private value class PlayerData(val rating: Int)

    private fun loadPlayerData(name: String, uuid: UUID) = PluginScope.launch(limitedDispatcher) {
        databaseApi.fetchUserRatings(name).getOrNull()?.sumOf { it.rating.rating }?.let {
            cached.put(uuid, PlayerData(rating = it))
        }
    }

    override fun getPlayerRating(name: String, uuid: UUID): Int {
        val data = cached.getIfPresent(uuid)
        if (data == null)
            loadPlayerData(name, uuid)
        return data?.rating ?: 0
    }
}