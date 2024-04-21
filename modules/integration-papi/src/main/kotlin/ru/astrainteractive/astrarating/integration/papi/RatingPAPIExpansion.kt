package ru.astrainteractive.astrarating.integration.papi

import org.bukkit.OfflinePlayer
import ru.astrainteractive.astralibs.expansion.KPlaceholderExpansion
import ru.astrainteractive.astrarating.integration.papi.di.PapiDependencies
import ru.astrainteractive.astrarating.integration.papi.placeholder.ColorPlaceholder
import ru.astrainteractive.astrarating.integration.papi.placeholder.RatingAmountPlaceholder
import ru.astrainteractive.astrarating.integration.papi.placeholder.api.RatingPlaceholder

internal class RatingPAPIExpansion(
    dependencies: PapiDependencies
) : KPlaceholderExpansion(
    "erating",
    "RomanMakeev",
    "1.0.0"
) {
    private val placeholders = listOf<RatingPlaceholder>(
        RatingAmountPlaceholder(dependencies),
        ColorPlaceholder(dependencies)
    )

    /**
     * erating_RomaRoman
     * erating_rating
     */
    override fun onRequest(player: OfflinePlayer, params: String): String {
        val placeholder = placeholders.firstOrNull { it.key == params }
        return placeholder?.asPlaceholder(player).orEmpty()
    }
}
