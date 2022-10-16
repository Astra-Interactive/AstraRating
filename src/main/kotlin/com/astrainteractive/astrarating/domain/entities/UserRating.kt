package com.astrainteractive.astrarating.domain.entities

import com.astrainteractive.astrarating.domain.SQLDatabase.Companion.NON_EXISTS_KEY
import ru.astrainteractive.astralibs.database.ColumnInfo
import ru.astrainteractive.astralibs.database.Entity
import ru.astrainteractive.astralibs.database.PrimaryKey

@Entity(UserRating.TABLE)
data class UserRating(
    @ColumnInfo("user_rating_id")
    @PrimaryKey
    val id: Long = NON_EXISTS_KEY,
    @ColumnInfo("user_created_report")
    val userCreatedReport: Long,
    @ColumnInfo("reported_user")
    val reportedUser: Long,
    @ColumnInfo("rating")
    val rating: Int,
    @ColumnInfo("message")
    val message: String,
    @ColumnInfo("time")
    val time: Long = System.currentTimeMillis(),
) {
    companion object {
        const val TABLE = "users_ratings"
    }
}
