package ru.astrainteractive.astrarating.data.exposed.model

import java.util.UUID

data class UserModel(
    var minecraftUUID: UUID,
    val minecraftName: String,
)
