package ru.astrainteractive.astrarating.db.rating.entity

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object UserRatingTable : LongIdTable("users_ratings", "user_rating_id") {
    val userCreatedReport = reference("user_created_report", UserTable).nullable()
    val reportedUser = reference("reported_user", UserTable)
    val rating = integer("rating")
    val ratingTypeIndex = integer("rating_type_index")
    val message = text("message")
    val time = long("time")
}

class UserRatingDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserRatingDAO>(UserRatingTable)

    val userCreatedReport by UserDAO optionalReferencedOn UserRatingTable.userCreatedReport
    val reportedUser by UserDAO referencedOn UserRatingTable.reportedUser
    val rating by UserRatingTable.rating
    val ratingTypeIndex by UserRatingTable.ratingTypeIndex
    val message by UserRatingTable.message
    val time by UserRatingTable.time
}
