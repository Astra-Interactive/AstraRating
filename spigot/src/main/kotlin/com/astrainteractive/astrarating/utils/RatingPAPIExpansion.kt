package com.astrainteractive.astrarating.utils

import com.astrainteractive.astrarating.domain.api.CachedTotalRating
import com.astrainteractive.astrarating.modules.CachedTotalRatingModule
import me.clip.placeholderapi.PlaceholderAPI
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import ru.astrainteractive.astralibs.di.getValue


object RatingPAPIExpansion : KPlaceholderExpansion(
    "erating",
    "RomanMakeev",
    "1.0.0"
) {
    private val cachedTotalRating: CachedTotalRating by CachedTotalRatingModule

    /**
     * erating_RomaRoman
     * erating_rating
     */
    override fun onRequest(player: OfflinePlayer, params: String): String? {
        if (params == "rating")
            return cachedTotalRating.getPlayerRating(player?.name ?: "NULL", player.uniqueId).toString()
        return ""
    }

}