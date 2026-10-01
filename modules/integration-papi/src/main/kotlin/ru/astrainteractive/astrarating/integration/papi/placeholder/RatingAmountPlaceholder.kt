package ru.astrainteractive.astrarating.integration.papi.placeholder

import org.bukkit.OfflinePlayer
import ru.astrainteractive.astrarating.integration.papi.di.PapiDependencies
import ru.astrainteractive.astrarating.integration.papi.placeholder.api.RatingPlaceholder

internal class RatingAmountPlaceholder(
    dependencies: PapiDependencies
) : RatingPlaceholder, PapiDependencies by dependencies {

    override val key: String = "rating"
    override fun asPlaceholder(param: OfflinePlayer): String {
        val rating = ratingCachedDao.getPlayerRating(param.uniqueId)
        return "$rating"
    }
}
