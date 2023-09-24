package ru.astrainteractive.astrarating.integration.papi.placeholder

import org.bukkit.OfflinePlayer
import ru.astrainteractive.astrarating.integration.papi.di.PapiModule
import ru.astrainteractive.astrarating.integration.papi.placeholder.api.RatingPlaceholder

internal class RatingAmountPlaceholder(
    module: PapiModule
) : RatingPlaceholder, PapiModule by module {

    override val key: String = "rating"
    override fun asPlaceholder(param: OfflinePlayer): String {
        val playerName = param.name ?: return 0.toString()
        val rating = cachedApi.getPlayerRating(playerName, param.uniqueId)
        return "$rating"
    }
}
