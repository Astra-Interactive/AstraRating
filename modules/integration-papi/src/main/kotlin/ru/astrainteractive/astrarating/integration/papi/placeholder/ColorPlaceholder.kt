package ru.astrainteractive.astrarating.integration.papi.placeholder

import org.bukkit.OfflinePlayer
import ru.astrainteractive.astrarating.integration.papi.coloring.ColoringUtil
import ru.astrainteractive.astrarating.integration.papi.di.PapiDependencies
import ru.astrainteractive.astrarating.integration.papi.placeholder.api.RatingPlaceholder

internal class ColorPlaceholder(
    dependencies: PapiDependencies
) : RatingPlaceholder, PapiDependencies by dependencies {

    override val key: String = "color"

    override fun asPlaceholder(param: OfflinePlayer): String {
        val rating = ratingCachedDao.getPlayerRating(param.uniqueId)
        return ColoringUtil.findColoringByRating(
            colorings = papiConfiguration.colorings,
            rating = rating
        )?.color.orEmpty()
    }
}
