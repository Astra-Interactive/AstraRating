package com.astrainteractive.astrarating.dto


data class UserDTO(
    val id: Int,
    var minecraftUUID: String = "",
    val minecraftName: String = "",
    val discordID: String? = "",
    val lastUpdated: Long = System.currentTimeMillis(),
)