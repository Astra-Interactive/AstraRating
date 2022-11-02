package com.astrainteractive.astrarating.utils

import com.astrainteractive.astrarating.domain.api.CachedTotalRating
import com.astrainteractive.astrarating.modules.CachedTotalRatingModule
import me.clip.placeholderapi.PlaceholderAPI
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

object RatingPAPIExpansion : PlaceholderExpansion() {
    private val cachedTotalRating: CachedTotalRating
        get() = CachedTotalRatingModule.value
    override fun getIdentifier(): String = "erating"

    override fun getAuthor(): String = "RomanMakeev"

    override fun getVersion(): String = "1.0.0"

    /**
     * erating_RomaRoman
     * erating_rating
     */
    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        val params = PlaceholderAPI.setPlaceholders(player,params.replace("<","%").replace(">","%"))
        Bukkit.getOfflinePlayer(params).let {
            if (it.firstPlayed != 0L)
                return cachedTotalRating.getPlayerRating(it.name?:"NULL",it.uniqueId).toString()
        }
        player?.let{
            if (params=="rating")
                return cachedTotalRating.getPlayerRating(it.name?:"NULL",it.uniqueId).toString()
        }
        return ""
    }

}