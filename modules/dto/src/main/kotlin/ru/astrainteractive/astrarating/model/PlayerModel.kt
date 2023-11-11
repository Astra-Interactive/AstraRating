package ru.astrainteractive.astrarating.model

import ru.astrainteractive.astralibs.permission.Permissible
import java.util.UUID

data class PlayerModel(
    val uuid: UUID,
    val name: String,
    val permissible: Permissible?
)
