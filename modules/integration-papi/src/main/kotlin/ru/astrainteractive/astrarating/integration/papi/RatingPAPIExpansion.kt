package ru.astrainteractive.astrarating.integration.papi

import org.bukkit.OfflinePlayer
import ru.astrainteractive.astralibs.util.KPlaceholderExpansion
import ru.astrainteractive.astrarating.integration.papi.di.PapiModule
import ru.astrainteractive.astrarating.integration.papi.placeholder.ColorPlaceholder
import ru.astrainteractive.astrarating.integration.papi.placeholder.RatingAmountPlaceholder
import ru.astrainteractive.astrarating.integration.papi.placeholder.api.RatingPlaceholder

internal class RatingPAPIExpansion(
    module: PapiModule
) : KPlaceholderExpansion(
    "erating",
    "RomanMakeev",
    "1.0.0"
) {
    private val placeholders = listOf<RatingPlaceholder>(
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
