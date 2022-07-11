package com.astrainteractive.astrarating.sqldatabase.entities

import com.astrainteractive.astralibs.catching
import java.sql.ResultSet

data class UserRating(
    val id: Long = -1L,
    val userCreatedReport: Long,
    val reportedUser: Long,
    val rating: Int,
    val message: String,
    val time: Long = System.currentTimeMillis(),
) {
    companion object {
        val TABLE: String
            get() = "users_ratings"
        val id: EntityInfo
            get() = EntityInfo("id", "INTEGER", primaryKey = true, autoIncrement = true)
        val userCreatedReport: EntityInfo
            get() = EntityInfo("user_created_report", "INTEGER")
        val reportedUser: EntityInfo
            get() = EntityInfo("reported_user", "INTEGER")
        val rating: EntityInfo
            get() = EntityInfo("rating", "INTEGER")
        val message: EntityInfo
            get() = EntityInfo("message", "TEXT")
        val time: EntityInfo
            get() = EntityInfo("time", "INTEGER")

        val entities: List<EntityInfo>
            get() = listOf(id, userCreatedReport, reportedUser, rating, message, time)

        fun fromResultSet(rs: ResultSet?) = catching {
            rs?.let {
                return@catching UserRating(
                    id = it.getLong(id.name),
                    userCreatedReport = it.getLong(userCreatedReport.name),
                    reportedUser = it.getLong(reportedUser.name),
                    rating = it.getInt(rating.name),
                    message = it.getString(message.name),
                    time = it.getLong(time.name),
                )
            }
        }
    }
}
