package ru.astrainteractive.astrarating.integration.papi.placeholder.expansion

import org.bukkit.OfflinePlayer
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingCache
import ru.astrainteractive.astrarating.integration.papi.placeholder.api.RatingPlaceholder

internal class RatingAmountPlaceholder(
    private val ratingCache: RatingCache
) : RatingPlaceholder {
    override val key: String = "rating"

    override fun asPlaceholder(param: OfflinePlayer): String {
        val rating = ratingCache.getPlayerRating(param.uniqueId)
        return "$rating"
    }
}
