package com.astrainteractive.astrarating.sqldatabase.entities

import org.bukkit.OfflinePlayer
import java.sql.ResultSet

data class UserAndRating(
    val reportedPlayer: User,
    val userCreatedReport: User,
    val rating: UserRating
)