package ru.astrainteractive.astrarating.db.rating.entity

import org.jetbrains.exposed.dao.id.LongIdTable

object UserRatingTable : LongIdTable("users_ratings", "user_rating_id") {
    val userCreatedReport = reference("user_created_report", UserTable).nullable()
    val reportedUser = reference("reported_user", UserTable)
    val rating = integer("rating")
    val ratingTypeIndex = integer("rating_type_index")
    val message = text("message")
    val time = long("time")
}
