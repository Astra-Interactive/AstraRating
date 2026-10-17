package ru.astrainteractive.astrarating.integration.papi.placeholder.expansion

import org.bukkit.OfflinePlayer
import ru.astrainteractive.astralibs.expansion.KPlaceholderExpansion
import ru.astrainteractive.astralibs.expansion.PlaceholderExpansionFacade
import ru.astrainteractive.astrarating.integration.papi.placeholder.api.RatingPlaceholder

internal class RatingPlaceholderExpansion(
    private val placeholders: List<RatingPlaceholder>
) : PlaceholderExpansionFacade, KPlaceholderExpansion(
    "erating",
    "RomanMakeev",
    "1.0.0"
) {
    /**
     * The plugin registers this expansion itself, so `/papi reload` must keep it:
     * PlaceholderAPI unregisters every expansion that does not persist and only loads
     * the ones from its expansions folder again.
     */
    override fun persist(): Boolean = true

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        player ?: return null
        val placeholder = placeholders.firstOrNull { ratingPlaceholder -> ratingPlaceholder.key == params }
        return placeholder?.asPlaceholder(player).orEmpty()
    }
}
