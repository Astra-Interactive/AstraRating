package com.astrainteractive.astrarating.utils

import com.astrainteractive.astrarating.domain.api.CachedTotalRating
import com.astrainteractive.astrarating.modules.CachedTotalRatingModule
import com.astrainteractive.astrarating.modules.ConfigProvider
import com.astrainteractive.astrarating.utils.coloring.ColoringMapper
import com.astrainteractive.astrarating.utils.coloring.ColoringUtils
import org.bukkit.OfflinePlayer
import org.bukkit.event.player.PlayerQuitEvent
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.events.DSLEvent
import ru.astrainteractive.astralibs.utils.HEX
import java.util.UUID


object RatingPAPIExpansion : KPlaceholderExpansion(
    "erating",
    "RomanMakeev",
    "1.0.0"
) {
    private val cachedTotalRating: CachedTotalRating by CachedTotalRatingModule
    private val config by ConfigProvider
    private val cachedColorRating = CachedData<UUID, String>("#FFFFFF")
    /**
     * erating_RomaRoman
     * erating_rating
     */
    override fun onRequest(player: OfflinePlayer, params: String): String?{
        when (params) {
            "rating" -> {
                val rating = cachedTotalRating.getPlayerRating(player?.name ?: "NULL", player.uniqueId) ?: 0
                return "$rating"
            }
            "color" -> {

                val rating = cachedTotalRating.getPlayerRating(player?.name ?: "NULL", player.uniqueId)
                rating ?: return ""
                if (config.coloring.isEmpty()) return ""
                return cachedColorRating.getOrUpdate(player.uniqueId) {
                    val coloring = config.coloring.map(ColoringMapper::toDTO)
                    ColoringUtils.getColoringByRating(coloring, rating).color.HEX()
                }
            }
            else -> return ""
        }
    }
}
