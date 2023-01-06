package com.astrainteractive.astrarating.domain.entities.tables.dto

import com.astrainteractive.astrarating.domain.api.NON_EXISTS_KEY


data class UserDTO(
    val id: Long = NON_EXISTS_KEY,
    var minecraftUUID: String = "",
    val minecraftName: String = "",
    val discordID: String? = "",
    val lastUpdated: Long = System.currentTimeMillis(),
)