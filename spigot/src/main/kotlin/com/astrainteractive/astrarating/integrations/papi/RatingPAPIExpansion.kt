package com.astrainteractive.astrarating.integrations.papi

import com.astrainteractive.astrarating.integrations.papi.di.PapiModule
import com.astrainteractive.astrarating.integrations.papi.placeholders.ColorPlaceholder
import com.astrainteractive.astrarating.integrations.papi.placeholders.RatingAmountPlaceholder
import org.bukkit.OfflinePlayer
import ru.astrainteractive.astralibs.utils.KPlaceholderExpansion

class RatingPAPIExpansion(
    module: PapiModule
) : KPlaceholderExpansion(
    "erating",
    "RomanMakeev",
    "1.0.0"
) {
    private val placeholders = listOf(
        RatingAmountPlaceholder(module),
        ColorPlaceholder(module)
    )

    /**
     * erating_RomaRoman
     * erating_rating
     */
    override fun onRequest(player: OfflinePlayer, params: String): String {
        val placeholder = placeholders.firstOrNull { it.key == params } ?: return ""
        return placeholder.asPlaceholder(player)
    }
}
