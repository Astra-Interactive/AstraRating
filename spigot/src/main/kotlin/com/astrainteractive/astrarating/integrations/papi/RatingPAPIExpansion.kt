package com.astrainteractive.astrarating.integrations.papi

import com.astrainteractive.astrarating.integrations.papi.coloring.ColoringMapper
import com.astrainteractive.astrarating.integrations.papi.coloring.ColoringUtils
import com.astrainteractive.astrarating.integrations.papi.di.PapiModule
import org.bukkit.OfflinePlayer
import org.jetbrains.kotlin.com.google.common.cache.CacheBuilder
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astralibs.utils.KPlaceholderExpansion
import java.util.UUID
import java.util.concurrent.TimeUnit

class RatingPAPIExpansion(
    module: PapiModule
) : KPlaceholderExpansion(
    "erating",
    "RomanMakeev",
    "1.0.0"
) {
    private val cachedTotalRating by module.cachedApi
    private val config by module.config

    private val cachedColorRating = CacheBuilder
        .newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .build<UUID, String>()

    /**
     * erating_RomaRoman
     * erating_rating
     */
    override fun onRequest(player: OfflinePlayer, params: String): String? {
        when (params) {
            "rating" -> {
                val rating = cachedTotalRating.getPlayerRating(player.name ?: "NULL", player.uniqueId) ?: 0
                return "$rating"
            }

            "color" -> {
                val rating = cachedTotalRating.getPlayerRating(player.name ?: "NULL", player.uniqueId)
                if (config.coloring.isEmpty()) return "NONE"
                return cachedColorRating.getIfPresent(player.uniqueId) ?: let {
                    val coloring = config.coloring.map(ColoringMapper::toDTO)
                    val color = ColoringUtils.getColoringByRating(coloring, rating).color
                    cachedColorRating.put(player.uniqueId, color)
                    color
                }
            }

            else -> return ""
        }
    }
}
