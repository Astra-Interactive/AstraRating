package ru.astrainteractive.astrarating.integration.papi.placeholder.expansion

import ru.astrainteractive.astrarating.data.dao.rating.api.RatingCache
import ru.astrainteractive.astrarating.integration.papi.placeholder.api.RatingPlaceholder
import java.util.UUID

internal class RatingAmountPlaceholder(
    private val ratingCache: RatingCache
) : RatingPlaceholder {
    override val key: String = "rating"

    override fun asPlaceholder(playerUUID: UUID): String {
        val rating = ratingCache.resolvePlayerRating(playerUUID)
        return "$rating"
    }
}
