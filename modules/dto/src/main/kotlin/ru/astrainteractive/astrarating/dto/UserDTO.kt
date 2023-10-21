package ru.astrainteractive.astrarating.dto

data class UserDTO(
    val id: Long,
    var minecraftUUID: String = "",
    val minecraftName: String = "",
    val lastUpdated: Long = System.currentTimeMillis(),
)
