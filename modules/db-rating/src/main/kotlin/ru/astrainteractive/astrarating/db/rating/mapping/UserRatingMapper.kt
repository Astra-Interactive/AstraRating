package ru.astrainteractive.astrarating.db.rating.mapping

import ru.astrainteractive.astrarating.db.rating.entity.UserRatingEntity
import ru.astrainteractive.astrarating.dto.RatingType
import ru.astrainteractive.astrarating.dto.UserRatingDTO
import ru.astrainteractive.klibs.mikro.core.domain.Mapper

object UserRatingMapper : Mapper<UserRatingEntity, UserRatingDTO> {
    override fun fromDTO(it: UserRatingDTO): UserRatingEntity {
        error("Method not implemented!")
    }

    override fun toDTO(it: UserRatingEntity): UserRatingDTO = UserRatingDTO(
        id = it.id,
        userCreatedReport = it.userCreatedReport,
        reportedUser = it.reportedUser,
        rating = it.rating,
        message = it.message,
        ratingType = RatingType.values().getOrElse(it.ratingTypeIndex) { RatingType.values().first() },
        time = it.time
    )
}
