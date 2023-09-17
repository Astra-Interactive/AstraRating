package com.astrainteractive.astrarating.integrations.papi.placeholders

import com.astrainteractive.astrarating.integrations.papi.di.PapiModule
import com.astrainteractive.astrarating.integrations.papi.placeholders.api.RatingPlaceholder
import org.bukkit.OfflinePlayer

class RatingAmountPlaceholder(
    module: PapiModule
) : RatingPlaceholder, PapiModule by module {

    override val key: String = "rating"
    override fun asPlaceholder(param: OfflinePlayer): String {
        val playerName = param.name ?: return 0.toString()
        val rating = cachedApi.getPlayerRating(playerName, param.uniqueId)
        return "$rating"
    }
}
