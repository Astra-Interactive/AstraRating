package ru.astrainteractive.astrarating.integration.papi.placeholder.api

import org.bukkit.OfflinePlayer

internal interface RatingPlaceholder {
    val key: String
    fun asPlaceholder(param: OfflinePlayer): String
}
