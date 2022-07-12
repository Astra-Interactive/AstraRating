package com.astrainteractive.astrarating.sqldatabase.entities

import org.bukkit.OfflinePlayer
import java.sql.ResultSet

data class UserAndRating(
    val reportedPlayer: OfflinePlayer?,
    val userCreatedReport: User,
    val rating: UserRating
) {
    companion object {
        fun fromResultSet(rs: ResultSet?,player: OfflinePlayer?=null):UserAndRating? = rs?.let {
            val userCreatedReport = User.fromResultSet(it) ?: return@let null
            val rating = UserRating.fromResultSet(it) ?: return@let null
            return@let UserAndRating(player, userCreatedReport, rating)
        }


    }
}