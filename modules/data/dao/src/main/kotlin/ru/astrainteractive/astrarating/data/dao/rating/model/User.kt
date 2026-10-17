package ru.astrainteractive.astrarating.data.dao.rating.model

data class User(
    val id: Long,
    var minecraftUUID: String = "",
    val minecraftName: String = "",
    val lastUpdated: Long = System.currentTimeMillis(),
)
