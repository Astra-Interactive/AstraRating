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
     * PlaceholderAPI quirk: `/papi reload` unregisters every expansion that does not persist and only loads
     * the ones from its expansions folder again, but the plugin registers this one itself.
     */
    override fun persist(): Boolean = true

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        player ?: return null
        val placeholder = placeholders.firstOrNull { ratingPlaceholder -> ratingPlaceholder.key == params }
        return placeholder?.asPlaceholder(player.uniqueId).orEmpty()
    }
}
