package ru.astrainteractive.astrarating.integration.papi.placeholder.api

import java.util.UUID

internal interface RatingPlaceholder {
    val key: String

    fun asPlaceholder(playerUUID: UUID): String
}
