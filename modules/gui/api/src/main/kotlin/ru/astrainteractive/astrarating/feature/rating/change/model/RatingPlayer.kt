package ru.astrainteractive.astrarating.feature.rating.change.model

import ru.astrainteractive.astralibs.server.permission.KPermissible
import java.util.UUID

data class RatingPlayer(
    val uuid: UUID,
    val name: String,
    val permissible: KPermissible?,
    val hasPlayedBefore: Boolean
)
