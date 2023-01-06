package com.astrainteractive.astrarating.domain.entities.tables

import ru.astrainteractive.astralibs.orm.database.Column
import ru.astrainteractive.astralibs.orm.database.Constructable
import ru.astrainteractive.astralibs.orm.database.Entity
import ru.astrainteractive.astralibs.orm.database.Table


object UserRatingTable : Table<Long>("users_ratings") {
    override val id: Column<Long> = long("user_rating_id").primaryKey().autoIncrement()
    val userCreatedReport = long("user_created_report")
    val reportedUser = long("reported_user")
    val rating = integer("rating")
    val message = text("message")
    val time = long("time")
}


class UserRatingEntity : Entity<Long>(UserRatingTable) {
    val id by UserRatingTable.id
    val userCreatedReport by UserRatingTable.userCreatedReport
    val reportedUser by UserRatingTable.reportedUser
    val rating by UserRatingTable.rating
    val message by UserRatingTable.message
    val time by UserRatingTable.time
    companion object: Constructable<UserRatingEntity>(::UserRatingEntity)
}