package ru.astrainteractive.astrarating.data.exposed.model

import ru.astrainteractive.astralibs.server.permission.KPermissible
import java.util.UUID

data class PlayerModel(
    val uuid: UUID,
    val name: String,
    val permissible: KPermissible?,
    val hasPlayedBefore: Boolean
)
