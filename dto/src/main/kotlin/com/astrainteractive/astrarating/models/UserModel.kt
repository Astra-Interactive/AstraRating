package com.astrainteractive.astrarating.models

import java.util.UUID

data class UserModel(
    var minecraftUUID: UUID,
    val minecraftName: String,
    val discordID: String? = "",
)