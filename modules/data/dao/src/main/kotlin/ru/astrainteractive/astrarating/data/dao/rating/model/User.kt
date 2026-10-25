@file:Suppress("BareIdentifier")

package ru.astrainteractive.astrarating.data.dao.rating.model

import kotlin.time.Instant

data class User(
    val id: Long,
    val minecraftUUID: String,
    val minecraftName: String,
    val lastUpdated: Instant,
)
