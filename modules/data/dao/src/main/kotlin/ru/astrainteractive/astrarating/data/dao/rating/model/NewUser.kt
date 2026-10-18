package ru.astrainteractive.astrarating.data.dao.rating.model

import java.util.UUID

data class NewUser(
    val minecraftUUID: UUID,
    val minecraftName: String,
)
