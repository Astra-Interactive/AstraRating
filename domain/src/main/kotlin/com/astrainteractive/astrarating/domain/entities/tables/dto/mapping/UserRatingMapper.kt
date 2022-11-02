package com.astrainteractive.astrarating.domain.entities.tables.dto.mapping

import com.astrainteractive.astrarating.domain.entities.UserRating
import com.astrainteractive.astrarating.domain.entities.tables.UserRatingEntity
import ru.astrainteractive.astralibs.domain.mapping.IMapper

object UserRatingMapper : IMapper<UserRatingEntity, UserRating> {
    override fun fromDTO(it: UserRating): UserRatingEntity {
        return (null as UserRatingEntity)
    }

    override fun toDTO(it: UserRatingEntity): UserRating = UserRating(
        id = it.id,
        userCreatedReport = it.userCreatedReport,
        reportedUser = it.reportedUser,
        rating = it.rating,
        message = it.message,
        time = it.time
    )
}