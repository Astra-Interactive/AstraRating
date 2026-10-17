package ru.astrainteractive.astrarating.integration.papi.placeholder.expansion

import org.bukkit.OfflinePlayer
import ru.astrainteractive.astrarating.integration.papi.di.PapiDependencies
import ru.astrainteractive.astrarating.integration.papi.placeholder.api.RatingPlaceholder
import ru.astrainteractive.astrarating.integration.papi.placeholder.model.ColoringUtil

internal class ColorPlaceholder(
    dependencies: PapiDependencies
) : RatingPlaceholder, PapiDependencies by dependencies {

    override val key: String = "color"

    override fun asPlaceholder(param: OfflinePlayer): String {
        val rating = ratingCache.getPlayerRating(param.uniqueId)
        return ColoringUtil.findColoringByRating(
            colorings = papiConfiguration.colorings,
            rating = rating
        )?.color.orEmpty()
    }
}
