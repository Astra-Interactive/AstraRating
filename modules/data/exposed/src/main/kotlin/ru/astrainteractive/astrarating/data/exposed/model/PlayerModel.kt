package ru.astrainteractive.astrarating.data.exposed.model

import ru.astrainteractive.astralibs.permission.Permissible
import java.util.UUID

data class PlayerModel(
    val uuid: UUID,
    val name: String,
    val permissible: Permissible?,
    val firstPlayed: Long
)
