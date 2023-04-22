package com.astrainteractive.astrarating.integrations

import com.astrainteractive.astrarating.domain.api.CachedApiImpl
import com.astrainteractive.astrarating.integrations.coloring.ColoringMapper
import com.astrainteractive.astrarating.integrations.coloring.ColoringUtils
import com.astrainteractive.astrarating.modules.ServiceLocator
import org.bukkit.OfflinePlayer
import org.jetbrains.kotlin.com.google.common.cache.CacheBuilder
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.utils.HEX
import ru.astrainteractive.astralibs.utils.KPlaceholderExpansion
import java.util.UUID
import java.util.concurrent.TimeUnit


object RatingPAPIExpansion : KPlaceholderExpansion(
    "erating",
    "RomanMakeev",
    "1.0.0"
) {
    private val cachedTotalRating by ServiceLocator.cachedApi
    private val config by ServiceLocator.config
    private val cachedColorRating = CacheBuilder
        .newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build<UUID, String>()
    /**
     * erating_RomaRoman
     * erating_rating
     */
    override fun onRequest(player: OfflinePlayer, params: String): String?{
        when (params) {
            "rating" -> {
                val rating = cachedTotalRating.getPlayerRating(player.name ?: "NULL", player.uniqueId) ?: 0
                return "$rating"
            }
            "color" -> {
                val rating = cachedTotalRating.getPlayerRating(player.name ?: "NULL", player.uniqueId)
                if (config.coloring.isEmpty()) return ""
                return cachedColorRating.get(player.uniqueId){
                    val coloring = config.coloring.map(ColoringMapper::toDTO)
                    ColoringUtils.getColoringByRating(coloring, rating).color.HEX()
                }
            }
            else -> return ""
        }
    }
}
