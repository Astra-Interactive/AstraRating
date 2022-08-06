package com.astrainteractive.astrarating.api

import me.clip.placeholderapi.PlaceholderAPI
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

object RatingPAPIExpansion : PlaceholderExpansion() {
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
                return CachedTotalRating.getPlayerRating(it).toString()
        }
        player?.let{
            if (params=="rating")
                return CachedTotalRating.getPlayerRating(it).toString()
        }
        return null
    }

}