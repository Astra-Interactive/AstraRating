package ru.astrainteractive.astrarating.db.rating.mapping

import ru.astrainteractive.astrarating.db.rating.entity.UserRatingDAO
import ru.astrainteractive.astrarating.dto.RatingType
import ru.astrainteractive.astrarating.dto.UserRatingDTO
import ru.astrainteractive.klibs.mikro.core.domain.Mapper

object UserRatingMapper : Mapper<UserRatingDAO, UserRatingDTO> {
    override fun fromDTO(it: UserRatingDTO): UserRatingDAO {
        error("Method not implemented!")
    }

    override fun toDTO(it: UserRatingDAO): UserRatingDTO = UserRatingDTO(
        id = it.id.value,
        userCreatedReport = it.userCreatedReport?.let(UserMapper::toDTO),
        reportedUser = it.reportedUser.let(UserMapper::toDTO),
        rating = it.rating,
        message = it.message,
        ratingType = RatingType.values().getOrElse(it.ratingTypeIndex) { RatingType.values().first() },
        time = it.time
    )
}
