package ru.astrainteractive.astrarating.integration.papi.placeholder.expansion

import org.bukkit.OfflinePlayer
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingCache
import ru.astrainteractive.astrarating.integration.papi.placeholder.api.RatingPlaceholder
import ru.astrainteractive.astrarating.integration.papi.placeholder.model.PapiConfig
import ru.astrainteractive.astrarating.integration.papi.placeholder.model.findByRating
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

internal class ColorPlaceholder(
    private val ratingCache: RatingCache,
    papiConfigKrate: CachedKrate<PapiConfig>
) : RatingPlaceholder {
    private val papiConfig by papiConfigKrate

    override val key: String = "color"

    override fun asPlaceholder(param: OfflinePlayer): String {
        val rating = ratingCache.getPlayerRating(param.uniqueId)
        return papiConfig.colorings.findByRating(rating)?.color.orEmpty()
    }
}
