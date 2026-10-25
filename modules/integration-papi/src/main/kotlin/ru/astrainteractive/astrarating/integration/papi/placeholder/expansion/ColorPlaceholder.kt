package ru.astrainteractive.astrarating.integration.papi.placeholder.expansion

import ru.astrainteractive.astrarating.data.dao.rating.api.RatingCache
import ru.astrainteractive.astrarating.integration.papi.placeholder.api.RatingPlaceholder
import ru.astrainteractive.astrarating.integration.papi.placeholder.model.PapiConfig
import ru.astrainteractive.astrarating.integration.papi.placeholder.util.findByRating
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import java.util.UUID

internal class ColorPlaceholder(
    private val ratingCache: RatingCache,
    papiConfigKrate: CachedKrate<PapiConfig>
) : RatingPlaceholder {
    private val papiConfig by papiConfigKrate

    override val key: String = "color"

    override fun asPlaceholder(playerUUID: UUID): String {
        val rating = ratingCache.resolvePlayerRating(playerUUID)
        return papiConfig.colorings.findByRating(rating)?.color.orEmpty()
    }
}
