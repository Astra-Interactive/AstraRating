package com.astrainteractive.astrarating.integrations.papi.placeholders.api

import org.bukkit.OfflinePlayer

interface RatingPlaceholder {
    val key: String
    fun asPlaceholder(param: OfflinePlayer): String
}
