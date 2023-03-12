package com.astrainteractive.astrarating.domain.entities.tables

import ru.astrainteractive.astralibs.orm.database.Column
import ru.astrainteractive.astralibs.orm.database.Constructable
import ru.astrainteractive.astralibs.orm.database.Entity
import ru.astrainteractive.astralibs.orm.database.Table


object UserRatingTable : Table<Int>("users_ratings") {
    override val id: Column<Int> = integer("user_rating_id").primaryKey().autoIncrement()
    val userCreatedReport = integer("user_created_report").nullable()
    val reportedUser = integer("reported_user")
    val rating = integer("rating")
    val ratingTypeIndex = integer("rating_type_index")
    val message = text("message",4096)
    val time = bigint("time")
}


class UserRatingEntity : Entity<Int>(UserRatingTable) {
    val id by UserRatingTable.id
    val userCreatedReport: Int? by UserRatingTable.userCreatedReport
    val reportedUser by UserRatingTable.reportedUser
    val rating by UserRatingTable.rating
    val ratingTypeIndex by UserRatingTable.ratingTypeIndex
    val message by UserRatingTable.message
    val time by UserRatingTable.time
    companion object: Constructable<UserRatingEntity>(::UserRatingEntity)
}